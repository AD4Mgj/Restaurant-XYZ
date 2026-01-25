package kul.example.service;

import kul.example.model.User;

public class UserSession {

    public enum ProductViewMode { ADMIN, USER }

    private static User loggedUser;

    private static Integer selectedProductId;
    private static ProductViewMode productViewMode = ProductViewMode.USER;

    public static void set(User user) {
        loggedUser = user;
    }

    public static User get() {
        return loggedUser;
    }

    public static void clear() {
        loggedUser = null;
        selectedProductId = null;
        productViewMode = ProductViewMode.USER;
    }

    public static Integer getSelectedProductId() {
        return selectedProductId;
    }

    public static void setSelectedProductId(Integer id) {
        selectedProductId = id;
    }

    public static ProductViewMode getProductViewMode() {
        return productViewMode;
    }

    public static void setProductViewMode(ProductViewMode mode) {
        productViewMode = (mode == null) ? ProductViewMode.USER : mode;
    }

    // ===== Cart navigation (skąd otwarto koszyk) =====
    private static String cartReturnView = null;

    public static void setCartReturnView(String fxmlPath) {
        cartReturnView = fxmlPath;
    }

    public static String getCartReturnView() {
        return cartReturnView;
    }

    public static void clearCartReturnView() {
        cartReturnView = null;
    }
}
