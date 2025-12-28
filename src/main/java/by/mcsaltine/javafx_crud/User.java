package by.mcsaltine.javafx_crud;

public class User {
    private int id;
    private String name;
    private String email;
    private int age;
    private int roleId;
    private String roleName;  // для удобства отображения

    public User(int id, String name, String email, int age, int roleId, String roleName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public boolean isAdmin() { return "admin".equalsIgnoreCase(roleName); }
}