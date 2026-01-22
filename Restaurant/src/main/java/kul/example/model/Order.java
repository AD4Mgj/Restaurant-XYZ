package kul.example.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order {
    private int id;
    private int userId;
    private List<OrderItem> orderItems;
    private double totalPrice;
    private Date date;

    public Order(int id, int userId, List<OrderItem> orderItems, double totalPrice, Date date) {
        this.id = id;
        this.userId = userId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderItems=" + orderItems +
                ", totalPrice=" + totalPrice +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && userId == order.userId && Double.compare(totalPrice, order.totalPrice) == 0 && Objects.equals(orderItems, order.orderItems) && Objects.equals(date, order.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, orderItems, totalPrice, date);
    }
}
