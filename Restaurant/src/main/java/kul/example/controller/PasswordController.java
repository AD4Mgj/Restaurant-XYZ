package kul.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import kul.example.utils.SceneUtils;

public class PasswordController {

    @FXML
    private TextField emailField;

    public void onNextClick(MouseEvent event) {
//        String email = emailField.getText();
        // TODO: walidacja
//        System.out.println("Podano e-mail: " + email);
        SceneUtils.changeScene(event,"/view/loginWithpassword.fxml");
    }


    public void onBackClick(ActionEvent actionEvent) {
        SceneUtils.changeScene("/view/login.fxml");
    }

    public void onForgotPasswordClick(MouseEvent mouseEvent) {
        // TODO: ekran resetu hasła
        System.out.println("Kliknięto 'Nie pamiętasz hasła?'");
    }

    public void dashboard(MouseEvent event) {
        SceneUtils.changeScene(event,"/view/dashboard.fxml");
    }
}
