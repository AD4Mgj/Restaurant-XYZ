package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import kul.example.database.CategoryDatabase;
import kul.example.database.ProductDatabase;
import kul.example.model.Category;
import kul.example.model.Product;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AddProductController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Label messageLabel;

    @FXML private ImageView previewImage;
    @FXML private Label plusLabel;

    @FXML private javafx.scene.layout.StackPane imagePicker;
    @FXML private javafx.scene.layout.StackPane emptyState;
    @FXML private Button removeImageBtn;

    // zapisujemy jako RESOURCE path, np. /images/uploads/xxx.jpg
    private String selectedImagePath = null;

    @FXML
    public void initialize() {
        List<Category> categories = CategoryDatabase.getAllCategories();
        categoryCombo.getItems().setAll(categories);

        categoryCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });

        if (!categories.isEmpty()) categoryCombo.getSelectionModel().select(0);
        if (messageLabel != null) messageLabel.setText("");

        // żeby ImageView nie rozciągał krzywo
        if (previewImage != null) {
            previewImage.setPreserveRatio(true);
            previewImage.setSmooth(true);
            previewImage.setCache(true);
        }
    }

    @FXML
    private void onChooseImage() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Wybierz zdjęcie dania");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Obrazy", "*.png", "*.jpg", "*.jpeg")
            );

            Stage stage = (Stage) nameField.getScene().getWindow();
            File file = chooser.showOpenDialog(stage);
            if (file == null) return;

            // zapis do projektu (żeby działało też u prowadzącego po odpaleniu z IDE)
            Path dir = Path.of("src/main/resources/images/uploads");
            Files.createDirectories(dir);

            String ext = getExtension(file.getName()); // ".jpg"/".jpeg"/".png"
            String safeName = file.getName()
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^A-Za-z0-9._-]", "");

            String finalName = System.currentTimeMillis() + "_" + safeName;
            // gwarancja że ma poprawne rozszerzenie
            if (!finalName.toLowerCase().endsWith(ext)) finalName += ext;

            Path dest = dir.resolve(finalName);
            Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            // zapisujemy jako ścieżkę resource, bo AdminDashboard już umie to wczytać
            selectedImagePath = "/images/uploads/" + finalName;

            // podgląd
            previewImage.setImage(new Image(dest.toUri().toString()));

            // stan UI
            if (emptyState != null) {
                emptyState.setVisible(false);
                emptyState.setManaged(false);
            }
            if (removeImageBtn != null) {
                removeImageBtn.setVisible(true);
                removeImageBtn.setManaged(true);
            }
            if (imagePicker != null && !imagePicker.getStyleClass().contains("image-picker-filled")) {
                imagePicker.getStyleClass().add("image-picker-filled");
            }

            showOk("Zdjęcie dodane.");

        } catch (Exception e) {
            showErr("Nie udało się dodać zdjęcia.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddProduct() {
        clearMsg();

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String priceRaw = priceField.getText() == null ? "" : priceField.getText().trim().replace(",", ".");
        Category cat = categoryCombo.getValue();

        if (name.isEmpty()) { showErr("Podaj nazwę dania."); return; }

        double price;
        try {
            price = Double.parseDouble(priceRaw);
        } catch (Exception e) {
            showErr("Niepoprawna cena (np. 19.99).");
            return;
        }

        if (cat == null) { showErr("Wybierz kategorię."); return; }

        if (selectedImagePath == null || selectedImagePath.isBlank()) {
            showErr("Dodaj zdjęcie dania.");
            return;
        }

        int id = ProductDatabase.getNextId();

        // UWAGA: poprawna kolejność konstruktora:
        // (id, name, price, imagePath, categoryId)
        Product p = new Product(id, name, price, selectedImagePath, cat.getId());
        ProductDatabase.addProduct(p);

        // zamknij okno
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void clearMsg() {
        if (messageLabel != null) messageLabel.setText("");
    }

    private void showErr(String msg) {
        if (messageLabel == null) return;
        messageLabel.setStyle("-fx-text-fill: #d6003a;");
        messageLabel.setText(msg);
    }

    private void showOk(String msg) {
        if (messageLabel == null) return;
        messageLabel.setStyle("-fx-text-fill: #1b8f3b;");
        messageLabel.setText(msg);
    }

    private String getExtension(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpeg")) return ".jpeg";
        if (lower.endsWith(".jpg")) return ".jpg";
        if (lower.endsWith(".png")) return ".png";
        return ".png";
    }

    @FXML
    private void onRemoveImage() {
        selectedImagePath = null;

        if (previewImage != null) previewImage.setImage(null);

        if (emptyState != null) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        }

        if (removeImageBtn != null) {
            removeImageBtn.setVisible(false);
            removeImageBtn.setManaged(false);
        }

        if (imagePicker != null) {
            imagePicker.getStyleClass().remove("image-picker-filled");
        }

        showOk("Zdjęcie usunięte.");
    }
}
