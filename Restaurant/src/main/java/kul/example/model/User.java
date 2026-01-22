package kul.example.model;

public class User {

    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private double wallet;
    private String role;

    public User(int id, String email, String password, String firstName, String lastName, double wallet, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.wallet = wallet;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getWallet() {
        return wallet;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toCSV() {
        return id + ";" + email + ";" + password + ";" + firstName + ";" + lastName + ";" + wallet + ";" + role;
    }
}