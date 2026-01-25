package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import kul.example.database.ProductDatabase;
import kul.example.model.Product;
import kul.example.service.UserSession;
import kul.example.utils.SceneUtils;
import kul.example.service.CartService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductProfileController {

    @FXML private ImageView bannerImage;

    @FXML private ImageView productImage;
    @FXML private Button imageEditBtn;

    @FXML private VBox viewBox;
    @FXML private VBox editBox;

    @FXML private Label nameLabel;
    @FXML private Label priceLabel;

    @FXML private TextField nameField;
    @FXML private TextField priceField;

    @FXML private Label totalLabel;
    @FXML private HBox totalRow;

    // USER (view)
    @FXML private HBox vegRow;
    @FXML private Label vegAdminLabel;
    @FXML private CheckBox vegCheck;
    @FXML private Label vegSurchargeLabel;
    @FXML private HBox userActionsRow;

    @FXML private Label sauceTitle;
    @FXML private FlowPane sauceChoicesPane;

    @FXML private Button cartBtn;
    @FXML private Label cartBadge;

    // ADMIN (edit)
    @FXML private CheckBox vegAvailableCheck;
    @FXML private TextField vegSurchargeField;

    @FXML private TextField newSauceField;
    @FXML private FlowPane sauceEditPane;
    @FXML private HBox adminActionsRow;

    @FXML private javafx.scene.layout.StackPane imageCard;

    private boolean editing = false;

    private Product product;
    private boolean adminMode = false;

    private boolean editMode = false;

    // user selections (nie zapisujemy do bazy)
    private ToggleGroup sauceGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        adminMode = UserSession.getProductViewMode() == UserSession.ProductViewMode.ADMIN;

        loadBanner();
        loadProductFromSession();

        if (product == null) return;

        setupModeUI();

        setupImagePencilPosition();
        setImageEditVisible(false); // na starcie ukryty

        updateCartBadge();
        renderView();
    }

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

    private void setupModeUI() {
        // ołówek tylko w trybie edycji admina
        if (imageEditBtn != null) {
            imageEditBtn.setVisible(false);
            imageEditBtn.setManaged(false);
        }

        if (!adminMode && adminActionsRow != null) {
            adminActionsRow.setVisible(false);
            adminActionsRow.setManaged(false);
        }

        // user button only for USER
        if (userActionsRow != null) {
            boolean showUserActions = !adminMode;
            userActionsRow.setVisible(showUserActions);
            userActionsRow.setManaged(showUserActions);
        }

        if (!adminMode) {
            editBox.setVisible(false);
            editBox.setManaged(false);
            viewBox.setVisible(true);
            viewBox.setManaged(true);
        }
    }

    private void loadBanner() {
        try (InputStream is = getClass().getResourceAsStream("/images/banner.png")) {
            if (is != null && bannerImage != null) {
                bannerImage.setImage(new Image(is));
            }
        } catch (Exception ignored) {}
    }

    private void loadProductFromSession() {
        Integer id = UserSession.getSelectedProductId();
        if (id == null) {
            goBack();
            return;
        }

        product = ProductDatabase.getAllProducts().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);

        if (product == null) {
            goBack();
        }
    }

    private void renderView() {
        nameLabel.setText(product.getName() == null ? "" : product.getName());
        priceLabel.setText(String.format("%.2f zł", product.getPrice()));

        nameField.setText(product.getName() == null ? "" : product.getName());
        priceField.setText(String.valueOf(product.getPrice()).replace(",", "."));

        loadImage(product.getImagePath());

        renderUserChoices();
        renderAdminEditFields();

        setupLiveTotalListeners();
        updateTotal();
    }

    private boolean totalListenersBound = false;

    private void setupLiveTotalListeners() {
        if (adminMode) {
            // dla admina możesz ukryć total (opcjonalnie)
            if (totalRow != null) {
                totalRow.setVisible(false);
                totalRow.setManaged(false);
            }
            return;
        }

        if (totalListenersBound) return;
        totalListenersBound = true;

        if (vegCheck != null) {
            vegCheck.selectedProperty().addListener((obs, oldV, newV) -> updateTotal());
        }

        if (sauceGroup != null) {
            sauceGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> updateTotal());
        }
    }

    private void updateTotal() {
        if (totalLabel == null || product == null) return;

        double total = product.getPrice();

        boolean vegSelected = vegCheck != null && vegCheck.isVisible() && vegCheck.isSelected();
        if (vegSelected) total += product.getVegSurcharge();

        totalLabel.setText(String.format("%.2f zł", total));
    }

    private void renderUserChoices() {
        boolean vegAvail = product.isVegAvailable();

        // --- WEGE ---
        if (vegRow != null) {
            vegRow.setVisible(vegAvail);
            vegRow.setManaged(vegAvail);
        }

        if (vegSurchargeLabel != null) {
            vegSurchargeLabel.setText(String.format("+%.2f zł", product.getVegSurcharge()));
        }

        // admin: bez checkboxa, user: z checkboxem
        if (adminMode) {
            if (vegCheck != null) {
                vegCheck.setVisible(false);
                vegCheck.setManaged(false);
            }
            if (vegAdminLabel != null) {
                vegAdminLabel.setVisible(true);
                vegAdminLabel.setManaged(true);
            }
        } else {
            if (vegCheck != null) {
                vegCheck.setVisible(true);
                vegCheck.setManaged(true);
            }
            if (vegAdminLabel != null) {
                vegAdminLabel.setVisible(false);
                vegAdminLabel.setManaged(false);
            }
        }

        // --- SOSY ---
        List<String> sauces = product.getSauces();
        boolean showSauces = sauces != null && !sauces.isEmpty();

        if (sauceTitle != null) {
            sauceTitle.setVisible(showSauces);
            sauceTitle.setManaged(showSauces);
        }

        if (sauceChoicesPane != null) {
            sauceChoicesPane.getChildren().clear();
            sauceChoicesPane.setVisible(showSauces);
            sauceChoicesPane.setManaged(showSauces);

            if (showSauces) {
                if (adminMode) {
                    // ADMIN: chipy z napisami (nieklikalne)
                    for (String s : sauces) {
                        Label chip = new Label(s);
                        chip.getStyleClass().add("chip-readonly");
                        chip.setFocusTraversable(false);
                        chip.setMouseTransparent(true);
                        sauceChoicesPane.getChildren().add(chip);
                    }
                } else {
                    // USER: radio wybór
                    sauceGroup = new ToggleGroup();
                    for (String s : sauces) {
                        RadioButton rb = new RadioButton(s);
                        rb.setToggleGroup(sauceGroup);
                        rb.getStyleClass().add("chip-radio");
                        sauceChoicesPane.getChildren().add(rb);
                        sauceGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> updateTotal());
                    }
                }
            }
        }
        updateTotal();
    }

    private void renderAdminEditFields() {
        if (!adminMode) return;

        if (vegAvailableCheck != null) {
            vegAvailableCheck.setSelected(product.isVegAvailable());
        }
        if (vegSurchargeField != null) {
            vegSurchargeField.setText(String.valueOf(product.getVegSurcharge()).replace(",", "."));
        }

        if (sauceEditPane != null) {
            sauceEditPane.getChildren().clear();
            List<String> sauces = product.getSauces();
            if (sauces != null) {
                for (String s : sauces) addSauceChipToEditPane(s);
            }
        }
    }

    private void addSauceChipToEditPane(String sauceName) {
        // prosty "chip" z X
        Button x = new Button("✕");
        x.getStyleClass().add("cat-del"); // masz już styl czerwonego kółka

        Label label = new Label(sauceName);
        label.getStyleClass().add("chip-label");

        HBox chip = new HBox(8, label, x);
        chip.getStyleClass().add("chip");
        chip.setStyle("-fx-alignment: center;");

        x.setOnAction(e -> {
            product.getSauces().removeIf(ss -> ss.equalsIgnoreCase(sauceName));
            renderAdminEditFields();
            renderUserChoices();
        });

        sauceEditPane.getChildren().add(chip);
    }

    private void loadImage(String path) {
        try {
            if (path == null || path.isBlank()) {
                productImage.setImage(null);
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

            productImage.setImage(image);

        } catch (Exception ignored) {}
    }

    private void setupImagePencilPosition() {
        if (imageCard == null || imageEditBtn == null) return;

        javafx.scene.layout.StackPane.setAlignment(imageEditBtn, Pos.TOP_RIGHT);
        javafx.scene.layout.StackPane.setMargin(imageEditBtn, new javafx.geometry.Insets(10, 10, 0, 0));
    }

    private void setImageEditVisible(boolean visible) {
        boolean show = adminMode && visible;
        if (imageEditBtn != null) {
            imageEditBtn.setVisible(show);
            imageEditBtn.setManaged(show);
        }
    }

    @FXML
    private void onAddToCart() {
        if (adminMode || product == null) return;

        boolean vegSelected = vegCheck != null && vegCheck.isVisible() && vegCheck.isSelected();
        double finalPrice = product.getPrice() + (vegSelected ? product.getVegSurcharge() : 0.0);

        // wybrany sos (radio)
        String sauceSelected = null;
        if (sauceGroup != null && sauceGroup.getSelectedToggle() instanceof RadioButton rb) {
            sauceSelected = rb.getText();
        }

        // nazwa do koszyka (żeby było widać co user wybrał)
        String name = product.getName() == null ? "" : product.getName();
        List<String> opts = new ArrayList<>();
        if (vegSelected) opts.add("Wege");
        if (sauceSelected != null && !sauceSelected.isBlank()) opts.add("Sos: " + sauceSelected);
        if (!opts.isEmpty()) name += " (" + String.join(", ", opts) + ")";

        // robimy „pozycję koszyka” jako nowy Product (bez grzebania w modelu koszyka)
        Product cartItem = new Product(
                product.getId(),
                name,
                finalPrice,
                product.getImagePath(),
                product.getCategoryId(),
                false,
                0.0,
                new ArrayList<>()
        );

        CartService.add(cartItem);
        showInfo("Koszyk", "Dodano do koszyka.");
        updateCartBadge();
    }

    @FXML
    private void onOpenCart() {
        UserSession.setCartReturnView("/view/productProfile.fxml");
        SceneUtils.changeScene("/view/cart.fxml");
    }

    private void goBack() {
        if (adminMode) SceneUtils.changeScene("/view/adminDashboard.fxml");
        else SceneUtils.changeScene("/view/userProducts.fxml");
    }

    @FXML
    private void onBack() {
        goBack();
    }

    @FXML
    private void onEdit() {
        if (!adminMode) return;
        editMode = true;

        viewBox.setVisible(false);
        viewBox.setManaged(false);

        editBox.setVisible(true);
        editBox.setManaged(true);

        if (imageEditBtn != null) {
            imageEditBtn.setVisible(true);
            imageEditBtn.setManaged(true);
        }
    }

    @FXML
    private void onCancelEdit() {
        if (!adminMode) return;
        editMode = false;

        if (imageEditBtn != null) {
            imageEditBtn.setVisible(false);
            imageEditBtn.setManaged(false);
        }

        editBox.setVisible(false);
        editBox.setManaged(false);

        viewBox.setVisible(true);
        viewBox.setManaged(true);

        renderView();
    }

    @FXML
    private void onSave() {
        if (!adminMode || product == null) return;

        String newName = nameField.getText() == null ? "" : nameField.getText().trim();
        if (newName.isEmpty()) {
            showInfo("Błąd", "Nazwa nie może być pusta.");
            return;
        }

        double newPrice;
        try {
            newPrice = Double.parseDouble(priceField.getText().trim().replace(",", "."));
        } catch (Exception e) {
            showInfo("Błąd", "Cena musi być liczbą (np. 35.50).");
            return;
        }

        boolean vegAvail = vegAvailableCheck != null && vegAvailableCheck.isSelected();
        double vegSurcharge = 0.0;
        if (vegSurchargeField != null && vegAvail) {
            try { vegSurcharge = Double.parseDouble(vegSurchargeField.getText().trim().replace(",", ".")); }
            catch (Exception e) {
                showInfo("Błąd", "Dopłata Wege musi być liczbą (np. 3.00).");
                return;
            }
        }

        product.setName(newName);
        product.setPrice(newPrice);
        product.setVegAvailable(vegAvail);
        product.setVegSurcharge(vegAvail ? vegSurcharge : 0.0);

        // zapis do CSV
        List<Product> all = ProductDatabase.getAllProducts();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == product.getId()) {
                all.set(i, product);
                break;
            }
        }
        ProductDatabase.saveAll(all);

        onCancelEdit();
    }

    @FXML
    private void onDelete() {
        if (!adminMode || product == null) return;

        List<Product> kept = ProductDatabase.getAllProducts().stream()
                .filter(p -> p.getId() != product.getId())
                .toList();

        ProductDatabase.saveAll(kept);
        SceneUtils.changeScene("/view/adminDashboard.fxml");
    }

    @FXML
    private void onChangeImage() {
        if (!adminMode || !editing || product == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Wybierz zdjęcie produktu");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrazy", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File chosen = fc.showOpenDialog(bannerImage.getScene().getWindow());
        if (chosen == null) return;

        try {
            // folder docelowy w resources (u Ciebie już działa logika /images/uploads/)
            Path uploadsDir = Path.of("src/main/resources/images/uploads");
            Files.createDirectories(uploadsDir);

            String safeName = chosen.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = System.currentTimeMillis() + "_" + safeName;

            Path target = uploadsDir.resolve(fileName);
            Files.copy(chosen.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

            // zapisujemy ścieżkę jako resource:
            String newPath = "/images/uploads/" + fileName;
            product.setImagePath(newPath);

            // zapis do CSV
            List<Product> all = ProductDatabase.getAllProducts();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId() == product.getId()) {
                    all.set(i, product);
                    break;
                }
            }
            ProductDatabase.saveAll(all);

            // odśwież UI
            loadImage(newPath);

        } catch (Exception e) {
            showInfo("Błąd", "Nie udało się zapisać zdjęcia: " + e.getMessage());
        }
    }

    @FXML
    private void onAddSauce() {
        if (!adminMode || product == null) return;

        String s = (newSauceField == null || newSauceField.getText() == null) ? "" : newSauceField.getText().trim();
        if (s.isEmpty()) return;

        if (product.getSauces() == null) product.setSauces(new ArrayList<>());

        boolean exists = product.getSauces().stream().anyMatch(x -> x.equalsIgnoreCase(s));
        if (!exists) product.getSauces().add(s);

        newSauceField.clear();

        renderAdminEditFields();
        renderUserChoices();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}