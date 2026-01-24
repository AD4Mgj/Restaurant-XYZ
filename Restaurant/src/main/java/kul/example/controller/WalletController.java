package kul.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kul.example.model.User;
import kul.example.service.UserSession;
import kul.example.service.WalletService;
import kul.example.utils.SceneUtils;

public class WalletController {

    @FXML
    private Label balanceLabel;

    @FXML
    private TextField amountField;

    @FXML
    private Label errorLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = UserSession.get();

        if (currentUser == null) {
            balanceLabel.setText("Brak użytkownika");
            errorLabel.setText("Nikt nie jest zalogowany.");
            return;
        }

        updateBalanceLabel();
    }

    @FXML
    private void onBackClick() {
        SceneUtils.changeScene("/view/userDashboard.fxml");
    }

    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("%.2f zł", currentUser.getWallet()));
        errorLabel.setText("");
    }

    @FXML
    public void onDepositClick(ActionEvent event) {
        handleWalletChange(true);
    }

    @FXML
    public void onWithdrawClick(ActionEvent event) {
        handleWalletChange(false);
    }

    private void handleWalletChange(boolean deposit) {
        if (currentUser == null) return;

        String text = amountField.getText();
        if (text == null || text.isBlank()) {
            errorLabel.setText("Podaj kwotę.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(text.replace(",", "."));
        } catch (NumberFormatException e) {
            errorLabel.setText("Nieprawidłowa kwota.");
            return;
        }

        if (amount <= 0) {
            errorLabel.setText("Kwota musi być dodatnia.");
            return;
        }

        boolean success;

        if (deposit) {
            WalletService.deposit(currentUser, amount);
            success = true;
        } else {
            success = WalletService.withdraw(currentUser, amount);
            if (!success) {
                errorLabel.setText("Brak wystarczających środków.");
                return;
            }
        }

        updateBalanceLabel();
        amountField.clear();
    }
}