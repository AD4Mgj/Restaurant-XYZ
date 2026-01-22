package kul.example.database;

import kul.example.model.Category;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDatabase {

    private static final String FILE_PATH = "src/main/resources/database/Categories.csv";

    public static List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                list.add(new Category(
                        Integer.parseInt(parts[0]),
                        parts[1]
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

    public static void addCategory(String name) {
        int id = getNextId();

        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(id + ";" + name + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll(List<Category> categories) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {

            fw.write("id;name\n");

            for (Category c : categories) {
                fw.write(c.toCSV() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}