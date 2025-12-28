package by.mcsaltine.javafx_crud;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserDAO {

    public static void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, age, role_id, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getAge());
            pstmt.setInt(4, 2); // роль по умолчанию — user (id=2)
            pstmt.setString(5, ""); // пароль пустой (или потом добавим хэширование)
            pstmt.executeUpdate();
        }
    }

    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> list = FXCollections.observableArrayList();
        String sql = "SELECT u.*, r.name as role_name FROM users u " +
                "JOIN roles r ON u.role_id = r.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getInt("role_id"),
                        rs.getString("role_name")
                ));
            }
        }
        return list;
    }

    public static void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, age = ?, role_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getAge());
            pstmt.setInt(4, user.getRoleId());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public static void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);  // потом замени на BCrypt.hashpw(newPassword, BCrypt.gensalt())
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public static void insertUserWithPassword(User user, String password) throws SQLException {
        String sql = "INSERT INTO users (name, email, age, role_id, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getAge());
            pstmt.setInt(4, user.getRoleId());
            pstmt.setString(5, password);  // потом хэшировать
            pstmt.executeUpdate();
        }
    }

    public static ObservableList<Course> getAllCourses() throws SQLException {
        ObservableList<Course> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Course(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        }
        return list;
    }

    public static ObservableList<Course> getEnrolledCourses(int userId) throws SQLException {
        ObservableList<Course> list = FXCollections.observableArrayList();
        String sql = "SELECT c.* FROM courses c " +
                "JOIN user_courses uc ON c.id = uc.course_id " +
                "WHERE uc.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Course(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        }
        return list;
    }

    public static void enrollUserInCourse(int userId, int courseId) throws SQLException {
        String sql = "INSERT IGNORE INTO user_courses (user_id, course_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
        }
    }

    public static void unenrollUserFromCourse(int userId, int courseId) throws SQLException {
        String sql = "DELETE FROM user_courses WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
        }
    }

    public static void addCourse(String title, String description) throws SQLException {
        String sql = "INSERT INTO courses (title, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        }
    }

    public static void deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.executeUpdate();
        }
    }
}
