package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import kul.example.database.LoginDatabase;
import kul.example.model.User;
import kul.example.service.UserSession;
import kul.example.utils.SceneUtils;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Hyperlink resetLink;

    @FXML
    private void onNextClick() {
        String email = emailField.getText();

        User user = LoginDatabase.findUserByEmail(email);

        if (user == null) {
            messageLabel.setText("Nie znaleziono konta o takim adresie e-mail");
            return;
        }

        UserSession.set(user);

        SceneUtils.changeScene("/view/loginPassword.fxml");
    }

    @FXML
    private void onBackClick() {
        SceneUtils.changeScene("/view/welcomeScreen.fxml");
    }

}