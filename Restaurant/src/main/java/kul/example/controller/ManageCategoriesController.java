package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import kul.example.database.CategoryDatabase;
import kul.example.model.Category;
import kul.example.utils.SceneUtils;

import java.util.List;

public class ManageCategoriesController {

    @FXML
    private ListView<Category> categoryList;

    @FXML
    private TextField newCategoryField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        categoryList.getItems().clear();
        List<Category> categories = CategoryDatabase.getAllCategories();
        categoryList.getItems().addAll(categories);
    }

    @FXML
    private void onAddCategory() {
        String name = newCategoryField.getText().trim();

        if (name.isEmpty()) {
            messageLabel.setText("Podaj nazwę kategorii");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        CategoryDatabase.addCategory(name);
        newCategoryField.clear();
        messageLabel.setText("Dodano kategorię");
        messageLabel.setStyle("-fx-text-fill: green;");

        loadCategories();
    }

    @FXML
    private void onDeleteCategory() {
        Category selected = categoryList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setText("Wybierz kategorię do usunięcia");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        List<Category> categories = CategoryDatabase.getAllCategories();
        categories.removeIf(c -> c.getId() == selected.getId());

        CategoryDatabase.saveAll(categories);

        messageLabel.setText("Usunięto kategorię");
        messageLabel.setStyle("-fx-text-fill: green;");

        loadCategories();
    }

    @FXML
    private void onBack() {
        SceneUtils.changeScene("/view/adminDashboard.fxml");
    }
}