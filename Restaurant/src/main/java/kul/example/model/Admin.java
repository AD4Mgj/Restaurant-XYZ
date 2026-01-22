package kul.example.model;

public class Admin extends User {

    public Admin(int id, String email, String password, String firstName, String lastName, double wallet) {
        super(id, email, password, firstName, lastName, wallet, "ADMIN");
    }
}
