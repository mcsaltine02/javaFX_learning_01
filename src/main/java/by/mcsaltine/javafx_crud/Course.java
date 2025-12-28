package by.mcsaltine.javafx_crud;

public class Course {
    private int id;
    private String title;
    private String description;

    public Course(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return title;
    }
}