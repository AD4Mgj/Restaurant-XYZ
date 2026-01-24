package kul.example.service;

import kul.example.database.UserDatabase;
import kul.example.model.User;

public class WalletService {

    public static boolean canAfford(User user, double amount) {
        return user.getWallet() >= amount;
    }

    public static void deposit(User user, double amount) {
        if (amount <= 0) return;

        user.setWallet(user.getWallet() + amount);
        UserDatabase.updateUserWallet(user);
    }

    public static boolean withdraw(User user, double amount) {
        if (amount <= 0) return false;

        if (!canAfford(user, amount)) {
            return false;
        }

        user.setWallet(user.getWallet() - amount);
        UserDatabase.updateUserWallet(user);
        return true;
    }
}