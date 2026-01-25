package kul.example.repository;

import kul.example.model.Order;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class OrderRepository {

    private static final String FILE = "src/main/resources/database/orders.csv";

    public static synchronized void save(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, true))) {
            writer.write(
                    order.getId() + ";" +
                            order.getUserEmail() + ";" +
                            order.getAddress() + ";" +
                            order.getSubtotal() + ";" +
                            order.getDelivery() + ";" +
                            order.getService() + ";" +
                            order.getTotal() + ";" +
                            order.getTimestamp() + ";" +
                            order.getProducts()
            );
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> findByUser(String email) {
        List<Order> list = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(";");
                if (parts.length < 9) continue;

                if (parts[1].equals(email)) {
                    list.add(new Order(
                            Long.parseLong(parts[0]),
                            parts[1],
                            parts[2],
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            Double.parseDouble(parts[5]),
                            Double.parseDouble(parts[6]),
                            parts[7],
                            parts[8]
                    ));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static long nextId() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE));
            if (lines.size() <= 1) return 1;

            String last = lines.get(lines.size() - 1);
            return Long.parseLong(last.split(";")[0]) + 1;

        } catch (Exception e) {
            return 1;
        }
    }

    public static List<Order> findAll() {
        List<Order> list = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(";");

                if (parts.length < 9) continue;

                list.add(new Order(
                        Long.parseLong(parts[0]),
                        parts[1],
                        parts[2],
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        Double.parseDouble(parts[5]),
                        Double.parseDouble(parts[6]),
                        parts[7],
                        parts[8]
                ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}