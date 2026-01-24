package kul.example.service;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(50, 700);

                contentStream.showText(type + " for: " + username);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Address: " + address);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("Subtotal: %.2f zl", subtotal));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("Delivery: %.2f zl", delivery));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("Service fee: %.2f zl", service));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(String.format("TOTAL: %.2f zl", total));

                contentStream.endText();
            }

            document.save(docPath);
            System.out.println(type + " saved to: " + docPath);
        } catch (IOException e) {
            System.err.println("Error while creating " + type + ": " + e.getMessage());
        }
    }
}
