package kul.example.model;

import java.util.Objects;

public class Wallet {
    private double balance;

    public Wallet(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Double.compare(balance, wallet.balance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(balance);
    }
}
