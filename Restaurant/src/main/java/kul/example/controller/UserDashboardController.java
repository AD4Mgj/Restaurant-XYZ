package kul.example.controller;

import javafx.fxml.FXML;
import kul.example.utils.SceneUtils;

public class UserDashboardController {

    @FXML
    private void onBrowseMenu() {
        SceneUtils.changeScene("/view/userProducts.fxml");
    }

    @FXML
    private void onLogout() {
        SceneUtils.changeScene("/view/login.fxml");
    }
}