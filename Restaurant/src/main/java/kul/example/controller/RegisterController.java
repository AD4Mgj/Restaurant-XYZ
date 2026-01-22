package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import kul.example.model.User;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Wszystkie pola są wymagane");
            return;
        }

        if (!pass.equals(confirm)) {
            messageLabel.setText("Hasła nie są takie same");
            return;
        }

        // Tworzymy użytkownika
        User user = new User(0, email, pass, 0.0);

        // Tu możesz dodać zapis do bazy, pliku, listy itd.
        System.out.println("Zarejestrowano użytkownika: " + user);

        messageLabel.setText("Zarejestrowano pomyślnie");
    }
}