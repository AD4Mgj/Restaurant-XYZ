package kul.example.service;

import kul.example.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class CartService {

    private static final List<Product> cart = new ArrayList<>();

    public static void add(Product product) {
        cart.add(product);
    }

    public static int size() {
        return cart.size();
    }

    public static List<Product> getAll() {
        return new ArrayList<>(cart);
    }

    public static Map<Product, Long> getItemsWithCount() {
        return cart.stream()
                .collect(Collectors.groupingBy(p -> p, LinkedHashMap::new, Collectors.counting()));
    }

    public static double getSubtotal() {
        return cart.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public static double getDeliveryFee() {
        return 5.0; // stałe 5 zł
    }

    public static double getServiceFee() {
        return getSubtotal() * 0.05; // 5% subtotal
    }

    public static double getTotal() {
        return getSubtotal() + getDeliveryFee() + getServiceFee();
    }

    public static void clear() {
        cart.clear();
    }

    public static void removeOne(Product p) {
        if (p == null) return;

        for (int i = cart.size() - 1; i >= 0; i--) {
            if (cart.get(i).equals(p)) {
                cart.remove(i);
                return;
            }
        }
    }

    public static void removeAll(Product p) {
        if (p == null) return;

        cart.removeIf(item -> item.equals(p));
    }
}
