package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {

    @FXML
    private Label someLabel;

    @FXML
    private void initialize() {
        System.out.println("UserDashboardController initialized");
    }
}
