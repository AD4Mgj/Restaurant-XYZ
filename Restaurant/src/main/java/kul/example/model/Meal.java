package kul.example.model;

import java.util.Objects;

public class Meal {
    private int id;
    private String name;
    private double price;


    public Meal(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return id == meal.id && Double.compare(price, meal.price) == 0 && Objects.equals(name, meal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
}
