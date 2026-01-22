package kul.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import kul.example.database.CategoryDatabase;
import kul.example.database.ProductDatabase;
import kul.example.model.Category;
import kul.example.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class UserProductsController {

    @FXML
    private ComboBox<Category> categoryFilter;

    @FXML
    private FlowPane productContainer;

    @FXML
    public void initialize() {
        loadCategories();
        loadProducts(null);
    }

    private void loadCategories() {
        List<Category> categories = CategoryDatabase.getAllCategories();
        categoryFilter.getItems().addAll(categories);
    }

    private void loadProducts(Integer categoryId) {
        productContainer.getChildren().clear();

        List<Product> products = ProductDatabase.getAllProducts();

        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategoryId() == categoryId)
                    .collect(Collectors.toList());
        }

        for (Product p : products) {
            VBox box = createProductBox(p);
            productContainer.getChildren().add(box);
        }
    }

    private VBox createProductBox(Product product) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(200);
        box.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label name = new Label(product.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label price = new Label(product.getPrice() + " zł");
        price.setTextFill(Color.DARKGREEN);

        Label category = new Label("Kategoria: " + getCategoryName(product.getCategoryId()));

        Label image = new Label("Zdjęcie: " + product.getImagePath());

        box.getChildren().addAll(name, price, category, image);

        return box;
    }

    private String getCategoryName(int id) {
        return CategoryDatabase.getAllCategories()
                .stream()
                .filter(c -> c.getId() == id)
                .map(Category::getName)
                .findFirst()
                .orElse("Brak");
    }

    @FXML
    private void onFilterChanged() {
        Category selected = categoryFilter.getValue();
        if (selected != null) {
            loadProducts(selected.getId());
        }
    }

    @FXML
    private void onClearFilter() {
        categoryFilter.getSelectionModel().clearSelection();
        loadProducts(null);
    }
}