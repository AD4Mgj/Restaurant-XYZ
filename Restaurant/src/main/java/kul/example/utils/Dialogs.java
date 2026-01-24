package kul.example.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Dialogs {

    public static Optional<Boolean> showInvoiceChoiceDialog() {
        Alert invoiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        invoiceAlert.setTitle("Typ dokumentu");
        invoiceAlert.setHeaderText("Wybierz rodzaj dokumentu:");
        invoiceAlert.setContentText("Czy chcesz otrzymać fakturę czy paragon?");

        ButtonType buttonReceipt = new ButtonType("Paragon");
        ButtonType buttonInvoice = new ButtonType("Faktura");
        ButtonType buttonCancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);

        invoiceAlert.getButtonTypes().setAll(buttonReceipt, buttonInvoice, buttonCancel);

        Optional<ButtonType> result = invoiceAlert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == buttonInvoice) return Optional.of(true);   // faktura
            if (result.get() == buttonReceipt) return Optional.of(false); // paragon
        }

        return Optional.empty();
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
