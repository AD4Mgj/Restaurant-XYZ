package kul.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import kul.example.database.UserDatabase;
import kul.example.model.User;
import kul.example.utils.SceneUtils;

public class RegisterController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;

    private static String email;
    private static String password;
    private static String firstName;
    private static String lastName;

    @FXML
    private void goToCode() {
        email = emailField.getText();
        SceneUtils.changeScene("/view/registerCode.fxml");
    }

    @FXML
    private void goToPassword() {
        SceneUtils.changeScene("/view/registerPassword.fxml");
    }

    @FXML
    private void goToName() {
        password = passwordField.getText();
        SceneUtils.changeScene("/view/registerName.fxml");
    }

    @FXML
    private void goToDone() {
        firstName = firstNameField.getText();
        lastName = lastNameField.getText();
        SceneUtils.changeScene("/view/registerDone.fxml");
    }

    @FXML
    private void finishRegistration() {

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

        System.out.println("Zapisano użytkownika:");
        System.out.println(user.toCSV());
    }
}