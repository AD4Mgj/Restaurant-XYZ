package kul.example.service;

import kul.example.model.Order;
import kul.example.model.Product;
import kul.example.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderService {

    public static void saveOrder(String email, String address,
                                 double subtotal, double delivery,
                                 double service, double total) {

        long id = OrderRepository.nextId();
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String products = CartService.getItemsWithCount().entrySet().stream()
                .map(e -> {
                    Product p = e.getKey();
                    long qty = e.getValue();

                    String sauces = (p.getSauces() != null && !p.getSauces().isEmpty())
                            ? String.join(", ", p.getSauces())
                            : "brak";

                    return qty + "x " + p.getName() + " (sosy: " + sauces + ")";
                })
                .reduce((a, b) -> a + " | " + b)
                .orElse("");

        Order order = new Order(
                id,
                email,
                address,
                subtotal,
                delivery,
                service,
                total,
                timestamp,
                products
        );

        OrderRepository.save(order);
    }
}