package kul.example.model;

import java.util.Objects;

public class OrderItem {
    private int mealId;
    private int quantity;

    public OrderItem(int mealId, int quantity) {
        this.mealId = mealId;
        this.quantity = quantity;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "mealId=" + mealId +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return mealId == orderItem.mealId && quantity == orderItem.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealId, quantity);
    }
}
