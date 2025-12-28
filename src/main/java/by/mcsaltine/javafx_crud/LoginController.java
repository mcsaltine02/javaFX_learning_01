package by.mcsaltine.javafx_crud;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField tfEmail;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblError;

    @FXML
    private void login() {
        String email = tfEmail.getText().trim();
        String password = tfPassword.getText();  // в реальности — хэшируй!

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Заполните все поля");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT u.*, r.name as role_name FROM users u " +
                    "JOIN roles r ON u.role_id = r.id " +
                    "WHERE u.email = ? AND u.password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);  // потом замени на BCrypt

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getInt("role_id"),
                        rs.getString("role_name")
                );
                CurrentUser.set(user);

                // Открываем главное окно
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/by/mcsaltine/javafx_crud/main.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Панель управления");
                stage.show();

                // Закрываем окно логина
                Stage loginStage = (Stage) tfEmail.getScene().getWindow();
                loginStage.close();
            } else {
                lblError.setText("Неверный email или пароль");
            }
        } catch (Exception e) {
            lblError.setText("Ошибка: " + e.getMessage());
        }
    }
}