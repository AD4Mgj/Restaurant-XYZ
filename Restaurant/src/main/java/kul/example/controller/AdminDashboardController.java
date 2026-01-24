package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.util.Optional;
import java.util.stream.Collectors;


import kul.example.database.CategoryDatabase;
import kul.example.database.ProductDatabase;
import kul.example.model.Category;
import kul.example.model.Product;
import kul.example.service.UserSession;
import kul.example.utils.SceneUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Path;


public class AdminDashboardController {

    @FXML private ImageView bannerImage;
    @FXML private TextField searchField;

    @FXML private VBox categoryContainer;
    @FXML private FlowPane productContainer;

    private Integer selectedCategoryId = null; // null = wszystkie

    @FXML private Button editCategoriesBtn;

    private boolean categoryEditMode = false;
    private boolean newCategoryEditorVisible = false;

    @FXML
    public void initialize() {
        loadBanner();
        loadCategories();
        loadProducts();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> loadProducts());
        }
    }

    private void loadBanner() {
        // Wrzuć banner do: src/main/resources/images/banner.png
        try (InputStream is = getClass().getResourceAsStream("/images/banner.png")) {
            if (is != null) {
                bannerImage.setImage(new Image(is));
            }
        } catch (Exception ignored) {}
    }

    private void loadCategories() {
        categoryContainer.getChildren().clear();

        List<Category> categories = CategoryDatabase.getAllCategories();

        // 1) „Wszystko”
        categoryContainer.getChildren().add(createCategoryTileAll());

        // 2) Kategorie z bazy
        for (Category c : categories) {
            categoryContainer.getChildren().add(createCategoryTile(c));
        }

        // 3) Plus na dole
        categoryContainer.getChildren().add(createAddCategoryTile());

        // 4) Jeśli user kliknął plus -> pokaż kafelek wpisywania nazwy (nad plusem)
        if (newCategoryEditorVisible) {
            int insertIndex = Math.max(0, categoryContainer.getChildren().size() - 1); // przed plusem
            categoryContainer.getChildren().add(insertIndex, createNewCategoryEditorTile());
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

        // aktywny na starcie:
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

        // czerwone kółko X (widoczne tylko w edit mode)
        Button del = new Button("✕");
        del.getStyleClass().add("cat-del");
        del.setVisible(categoryEditMode);
        del.setManaged(categoryEditMode);

        del.setOnAction(e -> {
            e.consume();
            confirmAndDeleteCategory(c);
        });

        StackPane tile = new StackPane(b, del);
        tile.setMaxWidth(Double.MAX_VALUE);

        StackPane.setAlignment(del, Pos.TOP_RIGHT);
        StackPane.setMargin(del, new Insets(6, 6, 0, 0));

        return tile;
    }

    private StackPane createAddCategoryTile() {
        Button plus = new Button("+");
        plus.getStyleClass().add("cat-btn");
        plus.setMaxWidth(Double.MAX_VALUE);

        plus.setOnAction(e -> {
            // pokaż edytor wpisywania nazwy
            if (!newCategoryEditorVisible) {
                newCategoryEditorVisible = true;
                loadCategories();
            }
        });

        StackPane tile = new StackPane(plus);
        tile.setMaxWidth(Double.MAX_VALUE);
        return tile;
    }

    private StackPane createNewCategoryEditorTile() {
        TextField tf = new TextField();
        tf.setPromptText("Nowa kategoria...");
        tf.getStyleClass().addAll("cat-btn", "cat-input");
        tf.setMaxWidth(Double.MAX_VALUE);

        // ENTER = zapisz, ESC = anuluj
        tf.setOnKeyPressed(ev -> {
            switch (ev.getCode()) {
                case ENTER -> saveNewCategory(tf.getText());
                case ESCAPE -> {
                    newCategoryEditorVisible = false;
                    loadCategories();
                }
            }
        });

        // klik gdzieś indziej: jeśli coś wpisane -> zapisz, inaczej anuluj
        tf.focusedProperty().addListener((obs, oldV, focused) -> {
            if (!focused) {
                String name = tf.getText() == null ? "" : tf.getText().trim();
                if (!name.isEmpty()) saveNewCategory(name);
                else {
                    newCategoryEditorVisible = false;
                    loadCategories();
                }
            }
        });

        StackPane tile = new StackPane(tf);
        tile.setMaxWidth(Double.MAX_VALUE);

        // focus po renderze
        javafx.application.Platform.runLater(tf::requestFocus);

        return tile;
    }

    private void saveNewCategory(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            newCategoryEditorVisible = false;
            loadCategories();
            return;
        }

        // opcjonalnie: blokada duplikatów (case-insensitive)
        boolean exists = CategoryDatabase.getAllCategories().stream()
                .anyMatch(c -> c.getName() != null && c.getName().trim().equalsIgnoreCase(name));
        if (exists) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Info");
            a.setHeaderText(null);
            a.setContentText("Taka kategoria już istnieje.");
            a.showAndWait();
            return;
        }

        CategoryDatabase.addCategory(name);

        newCategoryEditorVisible = false;
        loadCategories();
        loadProducts(); // żeby od razu działało filtrowanie
    }

    private void confirmAndDeleteCategory(Category c) {
        // policz ile dań ma ta kategoria
        long count = ProductDatabase.getAllProducts().stream()
                .filter(p -> p.getCategoryId() == c.getId())
                .count();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń kategorię");
        alert.setHeaderText("Na pewno chcesz usunąć kategorię: " + c.getName() + "?");

        if (count > 0) {
            alert.setContentText("Ta kategoria ma " + count + " dań. Usunięcie kategorii usunie też te dania.");
        } else {
            alert.setContentText("Ta kategoria nie ma dań.");
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        // 1) usuń produkty z tej kategorii (żeby nie zostały „sieroty”)
        List<Product> kept = ProductDatabase.getAllProducts().stream()
                .filter(p -> p.getCategoryId() != c.getId())
                .collect(Collectors.toList());
        ProductDatabase.saveAll(kept);

        // 2) usuń kategorię
        List<Category> catsKept = CategoryDatabase.getAllCategories().stream()
                .filter(x -> x.getId() != c.getId())
                .collect(Collectors.toList());
        CategoryDatabase.saveAll(catsKept);

        // reset filtra jeśli była wybrana ta kategoria
        if (selectedCategoryId != null && selectedCategoryId == c.getId()) {
            selectedCategoryId = null;
        }

        loadCategories();
        loadProducts();
    }

    @FXML
    private void onToggleCategoryEdit() {
        categoryEditMode = !categoryEditMode;

        if (editCategoriesBtn != null) {
            if (categoryEditMode) {
                editCategoriesBtn.setText("✔");
                editCategoriesBtn.getStyleClass().add("icon-btn-done");
            } else {
                editCategoriesBtn.setText("✎");
                editCategoriesBtn.getStyleClass().remove("icon-btn-done");
            }
        }

        // gdy wychodzisz z edit-mode, usuń ewentualny „kafelek wpisywania”
        newCategoryEditorVisible = false;
        loadCategories();
    }

    private void setActiveCategoryButton(Button active) {
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

        // KARTA "DODAJ"
        productContainer.getChildren().add(createAddCard());

        for (Product p : filtered) {
            productContainer.getChildren().add(createProductCard(p));
        }
    }

    private VBox createAddCard() {
        VBox card = new VBox();
        card.getStyleClass().addAll("product-card", "product-add-card");
        card.setPrefSize(170, 210);

        javafx.scene.control.Label plus = new javafx.scene.control.Label("+");
        plus.getStyleClass().add("add-plus");

        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.getChildren().add(plus);

        card.setOnMouseClicked(e -> openAddProductModal());
        return card;
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setPrefSize(170, 210);

        ImageView img = new ImageView();
        img.getStyleClass().add("product-img");
        img.setFitWidth(150);
        img.setFitHeight(110);
        img.setPreserveRatio(true);

        String path = p.getImagePath();

        try {
            if (path != null && !path.isBlank()) {
                Image image = null;

                if (path.startsWith("/")) {
                    // 1) próbuj jako resource
                    try (InputStream is = getClass().getResourceAsStream(path)) {
                        if (is != null) image = new Image(is);
                    }

                    // 2) jeśli resource nie istnieje (upload), próbuj jako plik w src/main/resources
                    if (image == null && path.startsWith("/images/uploads/")) {
                        File f = Path.of("src/main/resources" + path).toFile();
                        if (f.exists()) image = new Image(f.toURI().toString());
                    }
                } else {
                    // ścieżka jako normalny plik
                    File f = new File(path);
                    if (f.exists()) image = new Image(f.toURI().toString());
                }

                if (image != null) img.setImage(image);
            }
        } catch (Exception ignored) {}

        javafx.scene.control.Label name = new javafx.scene.control.Label(shorten(p.getName(), 22));
        name.getStyleClass().add("product-name");

        javafx.scene.control.Label price = new javafx.scene.control.Label(String.format("%.2f zł", p.getPrice()));
        price.getStyleClass().add("product-price");

        card.getChildren().addAll(img, name, price);

        card.setOnMouseClicked(e -> {
            UserSession.setSelectedProductId(p.getId());
            UserSession.setProductViewMode(UserSession.ProductViewMode.ADMIN);
            SceneUtils.changeScene("/view/productProfile.fxml");
        });

        return card;
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

    private void openAddProductModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addProduct.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            // USUWA “okno w oknie” (ramkę systemową). Masz własny X w UI.
            stage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Auto dopasowanie do zawartości (koniec ręcznego rozciągania)
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.showAndWait();

            // po zamknięciu odśwież produkty
            loadProducts();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        UserSession.clear();
        SceneUtils.changeScene("/view/welcomeScreen.fxml");
    }
}
