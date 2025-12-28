package by.mcsaltine.javafx_crud;

public class CurrentUser {
    private static User instance;

    public static void set(User user) {
        instance = user;
    }

    public static User get() {
        return instance;
    }

    public static boolean isLoggedIn() {
        return instance != null;
    }

    public static boolean isAdmin() {
        return instance != null && instance.isAdmin();
    }

    public static void logout() {
        instance = null;
    }
}