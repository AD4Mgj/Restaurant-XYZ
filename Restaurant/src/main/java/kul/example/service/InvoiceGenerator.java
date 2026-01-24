package kul.example.service;

import kul.example.model.Product;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class InvoiceGenerator {

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    public void generateInvoice(String username,
                                String address,
                                double subtotal,
                                double delivery,
                                double service,
                                double total) {
        generateDocument("INVOICE", username, address, subtotal, delivery, service, total);
    }

    public void generateReceipt(String username,
                                String address,
                                double subtotal,
                                double delivery,
                                double service,
                                double total) {
        generateDocument("RECEIPT", username, address, subtotal, delivery, service, total);
    }

    private void generateDocument(String type,
                                  String username,
                                  String address,
                                  double subtotal,
                                  double delivery,
                                  double service,
                                  double total) {

        String timestamp = getCurrentTimestamp();
        String filename = type.toLowerCase() + "_" + username + "_" + timestamp + ".pdf";
        String docPath = "src/main/resources/database/" + type.toLowerCase() + "s/" + filename;

        Map<Product, Long> items = CartService.getItemsWithCount();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(50, 750);

                //naglowek
                if ("RECEIPT".equals(type)) {
                    contentStream.showText("RESTAURACJA XYZ");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("ul. Konstantynow 12");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("20-708 Lublin");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("NIP: 123-456-78-90");
                    contentStream.newLineAtOffset(0, -25);
                    contentStream.showText("## PARAGON NIEFISKALNY ##");
                } else {
                    contentStream.showText("FAKTURA - RESTAURACJA XYZ");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("ul. Konstantynow 12, 20-708 Lublin");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("NIP: 123-456-78-90");
                    contentStream.newLineAtOffset(0, -25);
                    contentStream.showText("Nabywca: " + username);
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Adres dostawy: " + address);
                }

                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("SZCZEGOLY ZAMOWIENIA:");
                contentStream.newLineAtOffset(0, -15);

                if (items.isEmpty()) {
                    contentStream.showText("Brak pozycji (koszyk pusty).");
                } else {
                    for (Map.Entry<Product, Long> entry : items.entrySet()) {
                        Product p = entry.getKey();
                        long qty = entry.getValue();
                        double lineTotal = p.getPrice() * qty;

                        String itemPart  = String.format("%dx %s", qty, p.getName());
                        String pricePart = String.format("%.2f zl", p.getPrice());
                        String totalPart = String.format("%.2f zl", lineTotal);

                        String line = String.format("%-25s | %10s | %10s", itemPart, pricePart, totalPart);
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -15);
                    }
                }

                //podsumowanie
                contentStream.newLineAtOffset(0, -10);
                contentStream.showText("-----------------------------");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("Suma pozycji: %.2f zl", subtotal));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("Dostawa: %.2f zl", delivery));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("Oplata serwisowa: %.2f zl", service));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("-----------------------------");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText(String.format("RAZEM DO ZAPLATY: %.2f zl", total));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Platnosc z portfela uzytkownika.");

                contentStream.endText();
            }

            document.save(docPath);
            System.out.println(type + " saved to: " + docPath);
        } catch (IOException e) {
            System.err.println("Error while creating " + type + ": " + e.getMessage());
        }
    }
}
