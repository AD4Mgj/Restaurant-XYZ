package kul.example.model;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private int id;
    private String name;
    private double price;
    private String imagePath;
    private int categoryId;

    private boolean vegAvailable;
    private double vegSurcharge;
    private List<String> sauces = new ArrayList<>();

    public Product(int id, String name, double price, String imagePath, int categoryId) {
        this(id, name, price, imagePath, categoryId, false, 0.0, new ArrayList<>());
    }

    public Product(int id, String name, double price, String imagePath, int categoryId,
                   boolean vegAvailable, double vegSurcharge, List<String> sauces) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.categoryId = categoryId;
        this.vegAvailable = vegAvailable;
        this.vegSurcharge = vegSurcharge;
        this.sauces = (sauces == null) ? new ArrayList<>() : sauces;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public boolean isVegAvailable() {
        return vegAvailable;
    }

    public void setVegAvailable(boolean vegAvailable) {
        this.vegAvailable = vegAvailable;
    }

    public double getVegSurcharge() {
        return vegSurcharge;
    }

    public void setVegSurcharge(double vegSurcharge) {
        this.vegSurcharge = vegSurcharge;
    }

    public List<String> getSauces() {
        return sauces;
    }

    public void setSauces(List<String> sauces) {
        this.sauces = (sauces == null) ? new ArrayList<>() : sauces;
    }

    public String toCSV() {
        String saucesJoined = (sauces == null || sauces.isEmpty()) ? "" : String.join("|", sauces);
        return id + ";" + name + ";" + price + ";" + imagePath + ";" + categoryId + ";"
                + vegAvailable + ";" + vegSurcharge + ";" + saucesJoined;
    }
}