package kul.example.controller;

import javafx.fxml.FXML;
import kul.example.utils.SceneUtils;

public class AdminDashboardController {

    @FXML
    private void onAddProduct() {
        SceneUtils.changeScene("/view/addProduct.fxml");
    }

    @FXML
    private void onManageCategories() {
        SceneUtils.changeScene("/view/manageCategories.fxml");
    }

    @FXML
    private void onProductList() {
        SceneUtils.changeScene("/view/productList.fxml");
    }
}