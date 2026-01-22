package kul.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kul.example.database.CategoryDatabase;
import kul.example.database.ProductDatabase;
import kul.example.model.Category;
import kul.example.model.Product;
import kul.example.utils.SceneUtils;

import java.util.List;

public class ProductListController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, String> imageColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        imageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImagePath()));

        categoryColumn.setCellValueFactory(data -> {
            int categoryId = data.getValue().getCategoryId();
            Category category = CategoryDatabase.getAllCategories()
                    .stream()
                    .filter(c -> c.getId() == categoryId)
                    .findFirst()
                    .orElse(null);

            return new javafx.beans.property.SimpleStringProperty(
                    category != null ? category.getName() : "Brak"
            );
        });

        loadProducts();
    }

    private void loadProducts() {
        List<Product> products = ProductDatabase.getAllProducts();
        ObservableList<Product> observableList = FXCollections.observableArrayList(products);
        productTable.setItems(observableList);
    }

    @FXML
    private void onRefresh() {
        loadProducts();
    }

    @FXML
    private void onDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Wybierz produkt do usunięcia");
            return;
        }

        List<Product> products = ProductDatabase.getAllProducts();
        products.removeIf(p -> p.getId() == selected.getId());

        ProductDatabase.saveAll(products);

        loadProducts();
        showAlert("Produkt usunięty");
    }

    @FXML
    private void onBack() {
        SceneUtils.changeScene("/view/adminDashboard.fxml");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}