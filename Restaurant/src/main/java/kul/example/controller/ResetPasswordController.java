package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import kul.example.database.LoginDatabase;
import kul.example.database.UserDatabase;
import kul.example.model.User;
import kul.example.utils.SceneUtils;

public class ResetPasswordController {

    @FXML
    private TextField codeField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label messageLabel;

    private static User userToReset;

    public static void setUser(User user) {
        userToReset = user;
    }

    @FXML
    private void onCodeSubmit() {
        // kod może być dowolny
        SceneUtils.changeScene("/view/newPassword.fxml");
    }

    @FXML
    private void onPasswordChange() {
        String newPass = newPasswordField.getText();

        if (newPass.isEmpty()) {
            messageLabel.setText("Hasło nie może być puste");
            return;
        }

        userToReset.setPassword(newPass);
        UserDatabase.updateUserPassword(userToReset);

        messageLabel.setText("Hasło zostało zmienione!");
    }
}