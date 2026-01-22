package kul.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import kul.example.utils.SceneUtils;

public class LoginController {

    @FXML
    public void onLoginClick(MouseEvent event) {
        SceneUtils.changeScene(event, "/view/login.fxml");
    }

    @FXML
    public void onRegisterClick(ActionEvent actionEvent) {
        SceneUtils.changeScene("/view/registerEmail.fxml");
    }
}
