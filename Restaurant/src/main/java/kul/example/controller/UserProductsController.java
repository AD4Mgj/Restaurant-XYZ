package kul.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import kul.example.database.CategoryDatabase;
import kul.example.database.ProductDatabase;
import kul.example.model.Category;
import kul.example.model.Product;
import kul.example.model.User;
import kul.example.service.CartService;
import kul.example.service.UserSession;
import kul.example.utils.SceneUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserProductsController {

    // ===== MAIN UI =====
    @FXML private ImageView bannerImage;
    @FXML private TextField searchField;

    @FXML private VBox categoryContainer;
    @FXML private FlowPane productContainer; // musi pasować do fx:id w FXML

    // ===== Drawer (side menu) =====
    @FXML private VBox drawer;
    @FXML private Pane drawerOverlay;
    @FXML private Label drawerName;
    @FXML private Label drawerEmail;

    // ===== Cart badge =====
    @FXML private Label cartBadge;

    private Integer selectedCategoryId = null; // null = wszystkie

    @FXML
    public void initialize() {
        loadBanner();
        loadCategories();
        loadProducts();

        fillDrawerUserData();
        updateCartBadge();

        // wyszukiwanie: reaguje na wpisywanie
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> loadProducts());
        }

        // na start: drawer zamknięty (na pewno)
        closeDrawer();
    }

    // =========================
    // Banner / Categories / Products
    // =========================

    private void loadBanner() {
        try (InputStream is = getClass().getResourceAsStream("/images/banner.png")) {
            if (is != null && bannerImage != null) {
                bannerImage.setImage(new Image(is));
            }
        } catch (Exception ignored) {}
    }

    private void loadCategories() {
        if (categoryContainer == null) return;

        categoryContainer.getChildren().clear();
        categoryContainer.getChildren().add(createCategoryTileAll());

        List<Category> categories = CategoryDatabase.getAllCategories();
        for (Category c : categories) {
            categoryContainer.getChildren().add(createCategoryTile(c));
        }
    }

    private StackPane createCategoryTileAll() {
        Button allBtn = new Button("Wszystko");
        allBtn.getStyleClass().add("cat-btn");
        allBtn.setMaxWidth(Double.MAX_VALUE);

        allBtn.setOnAction(e -> {
            selectedCategoryId = null;
            setActiveCategoryButton(allBtn);
            loadProducts();
        });

        StackPane tile = new StackPane(allBtn);
        tile.setMaxWidth(Double.MAX_VALUE);

        // aktywny na start
        setActiveCategoryButton(allBtn);

        return tile;
    }

    private StackPane createCategoryTile(Category c) {
        Button b = new Button(c.getName());
        b.getStyleClass().add("cat-btn");
        b.setMaxWidth(Double.MAX_VALUE);

        b.setOnAction(e -> {
            selectedCategoryId = c.getId();
            setActiveCategoryButton(b);
            loadProducts();
        });

        StackPane tile = new StackPane(b);
        tile.setMaxWidth(Double.MAX_VALUE);
        return tile;
    }

    private void setActiveCategoryButton(Button active) {
        if (categoryContainer == null) return;

        for (var node : categoryContainer.getChildren()) {
            if (node instanceof StackPane sp) {
                for (var child : sp.getChildren()) {
                    if (child instanceof Button btn) {
                        btn.getStyleClass().remove("cat-btn-active");
                    }
                }
            }
        }
        active.getStyleClass().add("cat-btn-active");
    }

    private void loadProducts() {
        if (productContainer == null) return;

        productContainer.getChildren().clear();

        List<Product> products = ProductDatabase.getAllProducts();
        List<Product> filtered = new ArrayList<>();

        String query = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim().toLowerCase()
                : "";

        for (Product p : products) {
            boolean okCat = (selectedCategoryId == null) || p.getCategoryId() == selectedCategoryId;
            boolean okName = query.isEmpty() || (p.getName() != null && p.getName().toLowerCase().contains(query));
            if (okCat && okName) filtered.add(p);
        }

        for (Product p : filtered) {
            productContainer.getChildren().add(createProductCard(p));
        }
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setPrefSize(170, 210);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(12));

        ImageView img = new ImageView();
        img.getStyleClass().add("product-img");
        img.setFitWidth(150);
        img.setFitHeight(110);
        img.setPreserveRatio(true);

        loadImageInto(img, p.getImagePath());

        Label name = new Label(shorten(p.getName(), 22));
        name.getStyleClass().add("product-name");

        Label price = new Label(String.format("%.2f zł", p.getPrice()));
        price.getStyleClass().add("product-price");

        card.getChildren().addAll(img, name, price);

        card.setOnMouseClicked(e -> {
            UserSession.setSelectedProductId(p.getId());
            UserSession.setProductViewMode(UserSession.ProductViewMode.USER);
            SceneUtils.changeScene("/view/productProfile.fxml");
        });

        return card;
    }

    private void loadImageInto(ImageView target, String path) {
        try {
            if (path == null || path.isBlank()) {
                target.setImage(null);
                return;
            }

            Image image = null;

            if (path.startsWith("/")) {
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is != null) image = new Image(is);
                }

                if (image == null && path.startsWith("/images/uploads/")) {
                    File f = Path.of("src/main/resources" + path).toFile();
                    if (f.exists()) image = new Image(f.toURI().toString());
                }
            } else {
                File f = new File(path);
                if (f.exists()) image = new Image(f.toURI().toString());
            }

            target.setImage(image);
        } catch (Exception ignored) {}
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

    // =========================
    // Drawer logic
    // =========================

    private void fillDrawerUserData() {
        User u = UserSession.get(); // <-- TO JEST POPRAWNE W TWOIM PROJEKCIE

        if (u == null) {
            if (drawerName != null) drawerName.setText("Użytkownik");
            if (drawerEmail != null) drawerEmail.setText("");
            return;
        }

        String first = u.getFirstName() == null ? "" : u.getFirstName();
        String last  = u.getLastName()  == null ? "" : u.getLastName();
        String fullName = (first + " " + last).trim();
        if (fullName.isBlank()) fullName = "Użytkownik";

        if (drawerName != null) drawerName.setText(fullName);
        if (drawerEmail != null) drawerEmail.setText(u.getEmail() == null ? "" : u.getEmail());
    }

    @FXML
    private void onToggleDrawer() {
        if (drawer != null && drawer.isVisible()) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    @FXML
    private void onCloseDrawer() {
        closeDrawer();
    }

    private void openDrawer() {
        if (drawerOverlay != null) {
            drawerOverlay.setVisible(true);
            drawerOverlay.setManaged(true);
        }
        if (drawer != null) {
            drawer.setVisible(true);
            drawer.setManaged(true);
        }
    }

    private void closeDrawer() {
        if (drawerOverlay != null) {
            drawerOverlay.setVisible(false);
            drawerOverlay.setManaged(false);
        }
        if (drawer != null) {
            drawer.setVisible(false);
            drawer.setManaged(false);
        }
    }

    // =========================
    // Cart badge + navigation
    // =========================

    private void updateCartBadge() {
        if (cartBadge == null) return;

        int count = CartService.size();

        if (count <= 0) {
            cartBadge.setVisible(false);
            cartBadge.setManaged(false);
        } else {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisible(true);
            cartBadge.setManaged(true);
        }
    }

    @FXML
    private void onSearch() {
        loadProducts();
    }

    @FXML
    private void onOpenCart() {
        UserSession.setCartReturnView("/view/userProducts.fxml");
        SceneUtils.changeScene("/view/cart.fxml");
    }

    @FXML
    private void onOpenOrderHistory() {
        SceneUtils.changeScene("/view/orderHistory.fxml");
    }

    @FXML
    private void onOpenWallet() {
        SceneUtils.changeScene("/view/wallet.fxml");
    }

    @FXML
    private void onLogout() {
        UserSession.clear();
        SceneUtils.changeScene("/view/welcomeScreen.fxml");
    }
}
