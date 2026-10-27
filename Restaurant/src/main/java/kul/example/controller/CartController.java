package kul.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import kul.example.model.Product;
import kul.example.model.User;
import kul.example.service.*;
import kul.example.utils.Dialogs;
import kul.example.utils.SceneUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CartController {

    @FXML private FlowPane cartItemsPane;
    @FXML private Label totalAmountLabel;

    @FXML
    public void initialize() {
        renderCart();
        updateSummary();
    }

    // =========================
    // Render UI
    // =========================

    private void renderCart() {
        if (cartItemsPane == null) return;

        cartItemsPane.getChildren().clear();

        Map<Product, Long> items = CartService.getItemsWithCount();
        if (items == null || items.isEmpty()) {
            Label empty = new Label("Koszyk jest pusty.");
            empty.getStyleClass().add("cart-empty");
            cartItemsPane.getChildren().add(empty);
            return;
        }

        for (Map.Entry<Product, Long> entry : items.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue() == null ? 0 : entry.getValue().intValue();
            cartItemsPane.getChildren().add(createCartCard(p, qty));
        }
    }

    private Pane createCartCard(Product p, int qty) {
        // Card root
        HBox card = new HBox(14);
        card.getStyleClass().add("cart-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));
        card.setPrefWidth(310);     // żeby układało się ~3 w rzędzie jak na screenie
        card.setMinHeight(110);

        // Image
        StackPane imgWrap = new StackPane();
        imgWrap.getStyleClass().add("cart-img-wrap");
        imgWrap.setPrefSize(86, 64);

        ImageView img = new ImageView();
        img.getStyleClass().add("cart-img");
        img.setFitWidth(74);
        img.setFitHeight(54);
        img.setPreserveRatio(true);
        loadImageInto(img, p.getImagePath());

        imgWrap.getChildren().add(img);

        // Middle: name + chips
        VBox mid = new VBox(8);
        mid.setAlignment(Pos.TOP_LEFT);

        ParsedName parsed = parseName(p.getName());

        Label name = new Label(parsed.baseName);
        name.getStyleClass().add("cart-item-name");
        name.setWrapText(true);
        name.setMaxWidth(190);
        name.setMinWidth(0);

        VBox chipsCol = new VBox(6);
        chipsCol.setAlignment(Pos.TOP_LEFT);

        if (parsed.veg) {
            Label chipVeg = new Label("🟢 Wege");
            chipVeg.getStyleClass().addAll("cart-chip", "cart-chip-veg");
            chipsCol.getChildren().add(chipVeg);
        }
        if (parsed.sauce != null && !parsed.sauce.isBlank()) {
            Label chipSauce = new Label(parsed.sauce);
            chipSauce.getStyleClass().addAll("cart-chip", "cart-chip-sauce");
            chipsCol.getChildren().add(chipSauce);
        }

        for (String extra : parsed.extras) {
            Label chip = new Label(extra);
            chip.getStyleClass().addAll("cart-chip", "cart-chip-sauce");
            chipsCol.getChildren().add(chip);
        }

        mid.getChildren().addAll(name, chipsCol);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right: cena + qty controls (bez kosza)
        VBox right = new VBox(10);
        right.setAlignment(Pos.CENTER_RIGHT);

// Cena: (zmienia się razem z qty)
        HBox priceRow = new HBox(6);
        priceRow.setAlignment(Pos.CENTER_RIGHT);

        Label priceText = new Label("Cena:");
        priceText.getStyleClass().add("cart-price-label");

        Label priceValue = new Label(String.format("%.2f zł", p.getPrice() * qty));
        priceValue.getStyleClass().add("cart-price-value");

        priceRow.getChildren().addAll(priceText, priceValue);

// Ilość: - 1 +
        HBox qtyRow = new HBox(8);
        qtyRow.setAlignment(Pos.CENTER_RIGHT);

        Button minus = new Button("–");
        minus.getStyleClass().add("cart-qty-btn");
        minus.setOnAction(e -> {
            // jeśli było 1 -> usunie z koszyka (removeOne już to ogarnia)
            CartService.removeOne(p);
            renderCart();
            updateSummary();
        });

        Label qtyLabel = new Label(String.valueOf(qty));
        qtyLabel.getStyleClass().add("cart-qty-pill");

        Button plus = new Button("+");
        plus.getStyleClass().add("cart-qty-btn");
        plus.setOnAction(e -> {
            CartService.add(p);
            renderCart();
            updateSummary();
        });

        qtyRow.getChildren().addAll(minus, qtyLabel, plus);

        right.getChildren().addAll(priceRow, qtyRow);

        card.getChildren().addAll(imgWrap, mid, spacer, right);
        return card;
    }

    private void updateSummary() {
        if (totalAmountLabel == null) return;

        double subtotal = CartService.getSubtotal();
        totalAmountLabel.setText(String.format("%.2f zł", subtotal));
    }

    // Navigation

    @FXML
    private void onBack() {
        String backTo = UserSession.getCartReturnView();

        if (backTo == null || backTo.isBlank()) {
            backTo = "/view/userProducts.fxml";
        }

        UserSession.clearCartReturnView();
        SceneUtils.changeScene(backTo);
    }

    /**
     * Tymczasowo podpinamy pod stary flow zamawiania (dialogi),
     * żeby nic nie ruszać w logice. Checkout zrobimy później.
     */
    @FXML
    private void onCheckoutClick() {
        onOrderClickLegacy();
    }

    @FXML
    private void onClearCartClick() {
        CartService.clear();
        renderCart();
        updateSummary();
    }

    //ordering flow

    private void onOrderClickLegacy() {

        Optional<Boolean> docChoice = Dialogs.showInvoiceChoiceDialog();
        if (docChoice.isEmpty()) return;

        User currentUser = UserSession.get();
        if (currentUser == null) {
            Dialogs.showInfo("Brak zalogowanego użytkownika. Zaloguj się ponownie.");
            return;
        }

        TextInputDialog addressDialog = new TextInputDialog();
        addressDialog.setTitle("Adres dostawy");
        addressDialog.setHeaderText(null);
        addressDialog.setContentText("Podaj adres:");

        Optional<String> addrOpt = addressDialog.showAndWait();
        if (addrOpt.isEmpty()) return;

        String address = addrOpt.get().trim();
        if (address.isEmpty()) {
            Dialogs.showInfo("Adres nie może być pusty!");
            return;
        }

        double subtotal = CartService.getSubtotal();
        double delivery = CartService.getDeliveryFee();
        double service = CartService.getServiceFee();
        double total = CartService.getTotal();

        if (!WalletService.canAfford(currentUser, total)) {
            Dialogs.showInfo("Za mało środków w portfelu!");
            return;
        }

        boolean success = WalletService.withdraw(currentUser, total);
        if (!success) {
            Dialogs.showInfo("Nie udało się pobrać środków.");
            return;
        }

        InvoiceGenerator generator = new InvoiceGenerator();

        if (docChoice.get()) {
            generator.generateInvoice(currentUser.getEmail(), address, subtotal, delivery, service, total);
            Dialogs.showInfo("Wygenerowano fakturę.");
        } else {
            generator.generateReceipt(currentUser.getEmail(), address, subtotal, delivery, service, total);
            Dialogs.showInfo("Wygenerowano paragon.");
        }

        OrderService.saveOrder(
                currentUser.getEmail(),
                address,
                subtotal,
                delivery,
                service,
                total
        );

        CartService.clear();
        renderCart();
        updateSummary();
    }

    // Helpers

    private void loadImageInto(ImageView target, String path) {
        try {
            if (target == null) return;

            if (path == null || path.isBlank()) {
                target.setImage(null);
                return;
            }

            Image image = null;

            if (path.startsWith("/")) {
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is != null) image = new Image(is);
                }

                if (image == null && path.startsWith("/images/uploads/")) {
                    File f = Path.of("src/main/resources" + path).toFile();
                    if (f.exists()) image = new Image(f.toURI().toString());
                }
            } else {
                File f = new File(path);
                if (f.exists()) image = new Image(f.toURI().toString());
            }

            target.setImage(image);
        } catch (Exception ignored) {}
    }

    private static class ParsedName {
        String baseName;
        boolean veg;
        String sauce;
        List<String> extras = new ArrayList<>();
    }

    private ParsedName parseName(String full) {
        ParsedName out = new ParsedName();
        if (full == null) {
            out.baseName = "";
            return out;
        }

        String s = full.trim();
        int i1 = s.indexOf('(');
        int i2 = s.lastIndexOf(')');

        if (i1 >= 0 && i2 > i1) {
            out.baseName = s.substring(0, i1).trim();
            String inside = s.substring(i1 + 1, i2).trim();

            List<String> parts = Arrays.stream(inside.split(","))
                    .map(String::trim)
                    .filter(x -> !x.isBlank())
                    .collect(Collectors.toList());

            for (String p : parts) {
                if (p.equalsIgnoreCase("Wege")) {
                    out.veg = true;
                } else if (p.toLowerCase().startsWith("sos:")) {
                    out.sauce = p.substring(4).trim();
                } else {
                    out.extras.add(p);
                }
            }
        } else {
            out.baseName = s;
        }

        return out;
    }
}