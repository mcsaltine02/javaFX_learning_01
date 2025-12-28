package by.mcsaltine.javafx_crud;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TextField tfName;
    @FXML private TextField tfEmail;
    @FXML private TextField tfAge;
    @FXML private ListView<User> listView;
    @FXML private Button deleteButton;
    @FXML private ComboBox<String> cbRole;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblCurrentUser;

    @FXML private ListView<Course> lvMyCourses;
    @FXML private ListView<Course> lvAllCourses;
    @FXML private TextField tfCourseTitle;
    @FXML private TextField tfCourseDesc;
    @FXML private Pane adminCoursesPanel;

    @FXML private Pane addUserPanel;

    private ObservableList<User> userList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
        loadCourses();

        // Отображаем имя и роль текущего пользователя
        if (CurrentUser.isLoggedIn()) {
            User current = CurrentUser.get();
            lblCurrentUser.setText(current.getName() + " (" + current.getRoleName() + ")");
        }


        if (CurrentUser.isAdmin()) {
            addUserPanel.setVisible(true);
            addUserPanel.setManaged(true);
            cbRole.setItems(FXCollections.observableArrayList("user", "admin"));
            cbRole.setValue("user");

            deleteButton.setVisible(true);
            deleteButton.setManaged(true);
        } else {
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
        }

        listView.setCellFactory(param -> new UserCell());
        listView.setOnMouseClicked(this::handleListClick);


    }
    private void loadCourses() {
        try {
            int currentUserId = CurrentUser.get().getId();

            // Мои курсы
            ObservableList<Course> myCourses = UserDAO.getEnrolledCourses(currentUserId);
            lvMyCourses.setItems(myCourses);
            lvMyCourses.setCellFactory(param -> new CourseCell()); // ← добавь эту строку

            if (CurrentUser.isAdmin()) {
                adminCoursesPanel.setVisible(true);
                adminCoursesPanel.setManaged(true);

                ObservableList<Course> allCourses = UserDAO.getAllCourses();
                lvAllCourses.setItems(allCourses);

                // ← КАСТОМНАЯ ЯЧЕЙКА С КНОПКОЙ УДАЛЕНИЯ
                lvAllCourses.setCellFactory(param -> new CourseAdminCell(this));
            }
        } catch (SQLException e) {
            showAlert("Ошибка загрузки курсов: " + e.getMessage());
        }
    }
    public void deleteCourse(int courseId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удалить курс?");
        confirm.setContentText("Все пользователи будут автоматически отписаны. Продолжить?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            UserDAO.deleteCourse(courseId);
            loadCourses();  // обновляем списки
            showAlert("Курс успешно удалён!");
        } catch (SQLException e) {
            showAlert("Ошибка удаления курса: " + e.getMessage());
        }
    }

    public void loadData() {
        try {
            userList = UserDAO.getAllUsers();
            listView.setItems(userList);
        } catch (SQLException e) {
            showAlert("Ошибка загрузки данных: " + e.getMessage());
        }
        loadCourses();
    }

    @FXML
    private void addUser() {
        try {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            int age = Integer.parseInt(tfAge.getText());

            if (name.isEmpty() || email.isEmpty()) {
                showAlert("Имя и email обязательны!");
                return;
            }

            User newUser = new User(0, name, email, age, 2, "user");

            if (CurrentUser.isAdmin()) {
                String role = cbRole.getValue();
                newUser.setRoleId(role.equals("admin") ? 1 : 2);
                newUser.setRoleName(role);

                String password = tfPassword.getText();
                if (password.isEmpty()) {
                    showAlert("Для нового пользователя админ должен задать пароль!");
                    return;
                }
                // Сохраняем с паролем
                UserDAO.insertUserWithPassword(newUser, password);
            } else {
                UserDAO.insertUser(newUser); // обычный пользователь — без пароля или с дефолтным
            }

            loadData();
            clearFields();
        } catch (Exception e) {
            showAlert("Ошибка добавления: " + e.getMessage());
        }
    }

    @FXML
    private void deleteUser() {
        User selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите пользователя для удаления!");
            return;
        }

//         Дополнительно: можно добавить подтверждение
         Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить пользователя " + selected.getName() + "?");
         if (confirm.showAndWait().get() != ButtonType.OK) return;

        try {
            UserDAO.deleteUser(selected.getId());
            loadData();  // обновляем список пользователей
            loadCourses();  // если есть вкладка курсов — обновляем и её
        } catch (SQLException e) {
            showAlert("Ошибка удаления: " + e.getMessage());
        }
    }

    @FXML
    private void addCourse() {
        String title = tfCourseTitle.getText().trim();
        String description = tfCourseDesc.getText().trim();

        if (title.isEmpty()) {
            showAlert("Введите название курса!");
            return;
        }

        try {
            UserDAO.addCourse(title, description);
            tfCourseTitle.clear();
            tfCourseDesc.clear();

            // Обновляем список всех курсов (для админа)
            if (lvAllCourses != null) {
                ObservableList<Course> allCourses = UserDAO.getAllCourses();
                lvAllCourses.setItems(allCourses);
            }

            showAlert("Курс успешно добавлен!");
        } catch (SQLException e) {
            showAlert("Ошибка добавления курса: " + e.getMessage());
        }
    }

    private void handleListClick(MouseEvent event) {
        User selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null && event.getClickCount() == 2) { // двойной клик
            openDetailWindow(selected);
        }
    }

    private void openDetailWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/by/mcsaltine/javafx_crud/detail.fxml"));
            Parent root = loader.load();

            DetailController controller = loader.getController();
            controller.setUser(user, this); // передаём студента и ссылку на себя

            Stage stage = new Stage();
            stage.setTitle("Редактирование: " + user.getName());
            stage.initModality(Modality.WINDOW_MODAL); // модальное окно
            stage.initOwner(listView.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait(); // ждём закрытия
        } catch (Exception e) {
            showAlert("Ошибка открытия окна: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        // Очищаем текущего пользователя
        CurrentUser.logout();

        try {
            // Загружаем окно логина
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/by/mcsaltine/javafx_crud/login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Вход в систему");
            loginStage.setScene(new Scene(root, 400, 300));
            loginStage.show();

            // Закрываем главное окно
            Stage mainStage = (Stage) lblCurrentUser.getScene().getWindow();
            mainStage.close();

        } catch (Exception e) {
            showAlert("Ошибка при выходе: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tfName.clear();
        tfEmail.clear();
        tfAge.clear();
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
