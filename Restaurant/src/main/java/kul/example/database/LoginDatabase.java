package kul.example.database;

import kul.example.model.User;

import java.io.BufferedReader;
import java.io.FileReader;

public class LoginDatabase {

    private static final String FILE_PATH = "src/main/resources/database/Accounts.csv";

    public static User findUserByEmail(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length < 6) continue;

                String fileEmail = parts[1];

                if (fileEmail.equalsIgnoreCase(email)) {
                    return new User(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            Double.parseDouble(parts[5])
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}