package kul.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kul.example.model.Product;
import kul.example.model.User;
import kul.example.service.CartService;
import kul.example.service.InvoiceGenerator;
import kul.example.service.UserSession;
import kul.example.utils.Dialogs;
import kul.example.utils.SceneUtils;

import java.util.Map;
import java.util.Optional;

public class CartController {

    @FXML
    private TableView<CartItemRow> cartTable;

    @FXML
    private TableColumn<CartItemRow, String> nameColumn;

    @FXML
    private TableColumn<CartItemRow, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItemRow, String> priceColumn;

    @FXML
    private TableColumn<CartItemRow, String> totalColumn;

    @FXML
    private TextField addressField;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label deliveryLabel;

    @FXML
    private Label serviceLabel;

    @FXML
    private Label totalLabel;

    private final ObservableList<CartItemRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty());
        totalColumn.setCellValueFactory(data -> data.getValue().totalProperty());

        cartTable.setItems(rows);

        loadCart();
        updateSummary();
    }

    private void loadCart() {
        rows.clear();

        for (Map.Entry<Product, Long> entry : CartService.getItemsWithCount().entrySet()) {
            Product p = entry.getKey();
            long qty = entry.getValue();
            rows.add(new CartItemRow(p.getName(), (int) qty, p.getPrice()));
        }
    }

    private void updateSummary() {
        double subtotal = CartService.getSubtotal();
        double delivery = CartService.getDeliveryFee();
        double service = CartService.getServiceFee();
        double total = CartService.getTotal();

        subtotalLabel.setText(String.format("Suma: %.2f zł", subtotal));
        deliveryLabel.setText(String.format("Dostawa: %.2f zł", delivery));
        serviceLabel.setText(String.format("Opłata serwisowa: %.2f zł", service));
        totalLabel.setText(String.format("Do zapłaty: %.2f zł", total));
    }

    @FXML
    private void onBackClick() {
        SceneUtils.changeScene("/view/userProducts.fxml");
    }

    @FXML
    private void onClearCartClick() {
        CartService.clear();
        loadCart();
        updateSummary();
    }

    @FXML
    private void onOrderClick() {

        String address = addressField.getText().trim();

        if (address.isEmpty()) {
            Dialogs.showInfo("Adres nie może być pusty!");
            return;
        }

        // Wybór paragon / faktura
        Optional<Boolean> docChoice = Dialogs.showInvoiceChoiceDialog();

        if (docChoice.isEmpty()) {
            // anulowano
            return;
        }

        User currentUser = UserSession.get();
        if (currentUser == null) {
            Dialogs.showInfo("Brak zalogowanego użytkownika. Zaloguj się ponownie.");
            return;
        }

        String username = currentUser.getEmail();

        double subtotal = CartService.getSubtotal();
        double delivery = CartService.getDeliveryFee();
        double service = CartService.getServiceFee();
        double total = CartService.getTotal();

        InvoiceGenerator generator = new InvoiceGenerator();

        if (docChoice.get()) {
            generator.generateInvoice(username, address, subtotal, delivery, service, total);
            Dialogs.showInfo("Invoice generated.");
        } else {
            generator.generateReceipt(username, address, subtotal, delivery, service, total);
            Dialogs.showInfo("Receipt generated.");
        }

        CartService.clear();
        loadCart();
        updateSummary();
        addressField.clear();
    }


    public static class CartItemRow {

        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleIntegerProperty quantity;
        private final javafx.beans.property.SimpleStringProperty price;
        private final javafx.beans.property.SimpleStringProperty total;

        public CartItemRow(String name, int quantity, double price) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.quantity = new javafx.beans.property.SimpleIntegerProperty(quantity);
            this.price = new javafx.beans.property.SimpleStringProperty(String.format("%.2f zł", price));
            this.total = new javafx.beans.property.SimpleStringProperty(String.format("%.2f zł", price * quantity));
        }

        public javafx.beans.property.SimpleStringProperty nameProperty() { return name; }
        public javafx.beans.property.SimpleIntegerProperty quantityProperty() { return quantity; }
        public javafx.beans.property.SimpleStringProperty priceProperty() { return price; }
        public javafx.beans.property.SimpleStringProperty totalProperty() { return total; }
    }
}
