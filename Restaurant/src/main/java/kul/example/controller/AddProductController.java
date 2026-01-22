package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import kul.example.database.CategoryDatabase;
import kul.example.database.ProductDatabase;
import kul.example.model.Category;
import kul.example.model.Product;
import kul.example.utils.SceneUtils;

import java.io.File;
import java.util.List;

public class AddProductController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<Category> categoryCombo;

    @FXML
    private Label imagePathLabel;

    @FXML
    private Label messageLabel;

    private String selectedImagePath = null;

    @FXML
    public void initialize() {
        List<Category> categories = CategoryDatabase.getAllCategories();
        categoryCombo.getItems().addAll(categories);
    }

    @FXML
    private void onChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Wybierz zdjęcie produktu");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Obrazy", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selectedImagePath = "/images/" + file.getName();
            imagePathLabel.setText(file.getName());
        }
    }

    @FXML
    private void onAddProduct() {
        String name = nameField.getText();
        String priceText = priceField.getText();
        Category category = categoryCombo.getValue();

        if (name.isEmpty() || priceText.isEmpty() || category == null || selectedImagePath == null) {
            messageLabel.setText("Uzupełnij wszystkie pola!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            messageLabel.setText("Cena musi być liczbą!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        int id = ProductDatabase.getNextId();
        Product product = new Product(id, name, price, selectedImagePath, category.getId());
        ProductDatabase.addProduct(product);

        messageLabel.setText("Produkt dodany!");
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void onCancel() {
        SceneUtils.changeScene("/view/adminDashboard.fxml");
    }
}