package kul.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import kul.example.utils.SceneUtils;

public class UserDashboardController {

    @FXML
    private void onBrowseMenu() {
        SceneUtils.changeScene("/view/userProducts.fxml");
    }

    @FXML
    private void onLogout() {
        SceneUtils.changeScene("/view/welcomeScreen.fxml");
    }

    @FXML
    private void onOrderHistory() {
        SceneUtils.changeScene("/view/orderHistory.fxml");
    }

    public void onWallet() {
        SceneUtils.changeScene("/view/wallet.fxml");
    }
}