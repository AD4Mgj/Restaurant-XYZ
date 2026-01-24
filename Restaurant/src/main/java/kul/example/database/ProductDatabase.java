package kul.example.database;

import kul.example.model.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
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

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                double price = Double.parseDouble(parts[2].replace(",", "."));
                String imagePath = parts[3];
                int categoryId = Integer.parseInt(parts[4]);

                // domyślne wartości (dla starych CSV)
                boolean vegAvailable = false;
                double vegSurcharge = 0.0;
                List<String> sauces = new ArrayList<>();

                // jeśli CSV ma nowe kolumny:
                if (parts.length >= 6) vegAvailable = Boolean.parseBoolean(parts[5]);
                if (parts.length >= 7) vegSurcharge = safeDouble(parts[6]);
                if (parts.length >= 8 && parts[7] != null && !parts[7].isBlank()) {
                    sauces = new ArrayList<>(Arrays.asList(parts[7].split("\\|")));
                    sauces.removeIf(s -> s == null || s.isBlank());
                }

                list.add(new Product(id, name, price, imagePath, categoryId, vegAvailable, vegSurcharge, sauces));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static double safeDouble(String s) {
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
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

            fw.write("id;name;price;imagePath;categoryId;vegAvailable;vegSurcharge;sauces\n");

            for (Product p : products) {
                fw.write(p.toCSV() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
