package kul.example.database;

import kul.example.model.User;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserDatabase {

    private static final String FILE_PATH = "src/main/resources/database/Accounts.csv";

    public static int getNextId() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            if (lines.isEmpty()) return 1;

            String last = lines.get(lines.size() - 1);
            String[] parts = last.split(";");
            return Integer.parseInt(parts[0]) + 1;

        } catch (Exception e) {
            return 1;
        }
    }

    public static void saveUser(User user) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(user.toCSV() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserPassword(User user) {
        try {
            Path path = Paths.get(FILE_PATH);
            List<String> lines = Files.readAllLines(path);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 6) continue;

                if (parts[0].equals(String.valueOf(user.getId()))) {
                    parts[2] = user.getPassword();
                    lines.set(i, String.join(";", parts));
                    break;
                }
            }

            Files.write(path, lines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserWallet(User user) {
        try {
            Path path = Paths.get(FILE_PATH);
            List<String> lines = Files.readAllLines(path);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 7) continue;

                if (parts[0].equals(String.valueOf(user.getId()))) {
                    parts[5] = String.valueOf(user.getWallet());
                    lines.set(i, String.join(";", parts));
                    break;
                }
            }

            Files.write(path, lines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}