package kul.example.model;

public class Order {
    private long id;
    private String userEmail;
    private String address;
    private double subtotal;
    private double delivery;
    private double service;
    private double total;
    private String timestamp;

    public Order(long id, String userEmail, String address,
                 double subtotal, double delivery, double service, double total,
                 String timestamp) {
        this.id = id;
        this.userEmail = userEmail;
        this.address = address;
        this.subtotal = subtotal;
        this.delivery = delivery;
        this.service = service;
        this.total = total;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getAddress() { return address; }
    public double getSubtotal() { return subtotal; }
    public double getDelivery() { return delivery; }
    public double getService() { return service; }
    public double getTotal() { return total; }
    public String getTimestamp() { return timestamp; }
}