package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import kul.example.database.LoginDatabase;
import kul.example.model.User;
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

    private static User currentUser;

    @FXML
    private void onNextClick() {
        String email = emailField.getText();

        User user = LoginDatabase.findUserByEmail(email);

        if (user == null) {
            messageLabel.setText("Nie znaleziono konta o takim adresie e-mail");
            return;
        }

        currentUser = user;

        SceneUtils.changeScene("/view/loginWithPassword.fxml");
    }

    @FXML
    private void dashboard() {
        String pass = passwordField.getText();

        if (!currentUser.getPassword().equals(pass)) {
            messageLabel.setText("Niepoprawne hasło");
            resetLink.setVisible(true);
            return;
        }

        // ADMIN
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            SceneUtils.changeScene("/view/adminDashboard.fxml");
            return;
        }

        // USER
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
        ResetPasswordController.setUser(currentUser);
        SceneUtils.changeScene("/view/resetPassword.fxml");
    }
}