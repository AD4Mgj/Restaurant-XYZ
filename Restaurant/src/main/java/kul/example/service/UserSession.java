package kul.example.service;

import kul.example.model.User;

public class UserSession {

    private static User loggedUser;

    public static void set(User user) {
        loggedUser = user;
    }

    public static User get() {
        return loggedUser;
    }

    public static void clear() {
        loggedUser = null;
    }
}

