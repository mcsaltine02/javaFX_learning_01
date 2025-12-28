package by.mcsaltine.javafx_crud;

public class Role {
    private int id;
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isAdmin() { return "admin".equalsIgnoreCase(name); }
}