package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kul.example.database.UserDatabase;
import kul.example.model.User;
import kul.example.utils.SceneUtils;
import javafx.event.ActionEvent;
import kul.example.service.UserSession;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label nameErrorLabel;

    private static String email;
    private static String password;
    private static String firstName;
    private static String lastName;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // ====== STEP 1: EMAIL -> PASSWORD ======
    @FXML
    private void goToPassword() {
        clearErrors();

        String value = emailField != null ? emailField.getText().trim() : "";

        if (value.isEmpty()) {
            showEmailError("Wpisz adres e-mail.");
            return;
        }

        if (value.length() > 254 || !EMAIL_PATTERN.matcher(value).matches()) {
            showEmailError("Niepoprawny format e-mail.");
            return;
        }

        if (emailExists(value)) {
            showEmailError("To konto już istnieje. Zaloguj się.");
            return;
        }

        email = value;
        SceneUtils.changeScene("/view/registerPassword.fxml");
    }

    // ====== STEP 2: PASSWORD -> NAME ======
    @FXML
    private void goToName() {
        clearErrors();

        String value = passwordField != null ? passwordField.getText() : "";

        if (value.isEmpty()) {
            showPasswordError("Wpisz hasło.");
            return;
        }

        if (!isValidPassword(value)) {
            showPasswordError("Hasło musi mieć min. 6 znaków oraz zawierać: 1 cyfrę, 1 małą i 1 dużą literę.");
            return;
        }

        password = value;
        SceneUtils.changeScene("/view/registerName.fxml");
    }

    // ====== STEP 3: NAME -> DONE ======
    @FXML
    private void goToDone() {
        clearErrors();

        String fn = firstNameField != null ? firstNameField.getText().trim() : "";
        String ln = lastNameField != null ? lastNameField.getText().trim() : "";

        if (fn.isEmpty() || ln.isEmpty()) {
            showNameError("Wpisz imię i nazwisko.");
            return;
        }

        // minimalna normalizacja (żeby nie było "   ")
        if (fn.length() < 2 || ln.length() < 2) {
            showNameError("Imię i nazwisko muszą mieć przynajmniej 2 znaki.");
            return;
        }

        firstName = fn;
        lastName = ln;

        SceneUtils.changeScene("/view/registerDone.fxml");
    }

    @FXML
    private void finishRegistration(ActionEvent event) {
        int id = UserDatabase.getNextId();

        User user = new User(
                id,
                email,
                password,
                firstName,
                lastName,
                0.0,
                "USER"
        );

        UserDatabase.saveUser(user);

        UserSession.set(user);

        SceneUtils.changeScene(event, "/view/userProducts.fxml");
    }

    // ====== BACK BUTTONS (MUSZĄ ISTNIEĆ, bo masz je w FXML) ======

    @FXML
    private void onBackToWelcome() {
        SceneUtils.changeScene("/view/welcomeScreen.fxml");
    }

    @FXML
    private void onBackToRegisterEmail() {
        SceneUtils.changeScene("/view/registerEmail.fxml");
    }

    @FXML
    private void onBackToRegisterPassword() {
        SceneUtils.changeScene("/view/registerPassword.fxml");
    }

    @FXML
    private void onBackToRegisterName() {
        SceneUtils.changeScene("/view/registerName.fxml");
    }

    // ====== HELPERS ======

    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) return false;

        boolean hasDigit = false;
        boolean hasLower = false;
        boolean hasUpper = false;

        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (Character.isDigit(c)) hasDigit = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
        }

        return hasDigit && hasLower && hasUpper;
    }

    private void clearErrors() {
        if (emailErrorLabel != null) emailErrorLabel.setText("");
        if (passwordErrorLabel != null) passwordErrorLabel.setText("");
        if (nameErrorLabel != null) nameErrorLabel.setText("");
    }

    private void showEmailError(String msg) {
        if (emailErrorLabel != null) emailErrorLabel.setText(msg);
    }

    private void showPasswordError(String msg) {
        if (passwordErrorLabel != null) passwordErrorLabel.setText(msg);
    }

    private void showNameError(String msg) {
        if (nameErrorLabel != null) nameErrorLabel.setText(msg);
    }

    private boolean emailExists(String email) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/database/Accounts.csv"));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String existingEmail = parts[1].trim();
                    if (existingEmail.equalsIgnoreCase(email)) return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }
}
