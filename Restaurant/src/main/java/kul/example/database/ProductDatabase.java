package kul.example.database;

import kul.example.model.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDatabase {

    private static final String FILE_PATH = "src/main/resources/database/Products.csv";

    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 5) continue;

                list.add(new Product(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        Double.parseDouble(parts[2]),
                        parts[3],
                        Integer.parseInt(parts[4])
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static int getNextId() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            if (lines.size() <= 1) return 1;

            String last = lines.get(lines.size() - 1);
            return Integer.parseInt(last.split(";")[0]) + 1;

        } catch (Exception e) {
            return 1;
        }
    }

    public static void addProduct(Product product) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(product.toCSV() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll(List<Product> products) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {

            fw.write("id;name;price;imagePath;categoryId\n");

            for (Product p : products) {
                fw.write(p.toCSV() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}