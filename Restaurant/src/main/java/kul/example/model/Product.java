package kul.example.model;

public class Product {

    private int id;
    private String name;
    private double price;
    private String imagePath;
    private int categoryId;

    public Product(int id, String name, double price, String imagePath, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.categoryId = categoryId;
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

    public String toCSV() {
        return id + ";" + name + ";" + price + ";" + imagePath + ";" + categoryId;
    }
}