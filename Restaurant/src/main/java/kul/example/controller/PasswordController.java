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

public class PasswordController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Hyperlink resetLink;

    User user = UserSession.get();

    @FXML
    private void onLoginClick() {
        String pass = passwordField.getText();
        User user = UserSession.get();

        if (user == null) {
            messageLabel.setText("Błąd: użytkownik nie został przekazany.");
            return;
        }

        if (!user.getPassword().equals(pass)) {
            messageLabel.setText("Niepoprawne hasło");
            resetLink.setVisible(true);
            return;
        }

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            SceneUtils.changeScene("/view/adminDashboard.fxml");
            return;
        }

        SceneUtils.changeScene("/view/userDashboard.fxml");
    }

    @FXML
    private void onBackClick() {
        SceneUtils.changeScene("/view/login.fxml");
    }

    @FXML
    private void onForgotPasswordClick() {
        messageLabel.setText("Funkcja niedostępna");
    }

    @FXML
    private void onResetPasswordClick() {
        ResetPasswordController.setUser(user);
        SceneUtils.changeScene("/view/resetPassword.fxml");
    }
}