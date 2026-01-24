package kul.example.service;

import kul.example.model.Order;
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

        Order order = new Order(id, email, address, subtotal, delivery, service, total, timestamp);

        OrderRepository.save(order);
    }
}