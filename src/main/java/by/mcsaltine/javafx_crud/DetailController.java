package by.mcsaltine.javafx_crud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class DetailController {

    @FXML private Label lblId;
    @FXML private TextField tfName;
    @FXML private TextField tfEmail;
    @FXML private TextField tfAge;

    @FXML private ComboBox<String> cbRole;
    @FXML private PasswordField tfNewPassword;

    @FXML private Label lblRole;
    @FXML private Label lblPassword;

    @FXML private Button btnSave;

    // === Элементы для курсов (только для админа) ===
    @FXML private Label lblUserCourses;
    @FXML private Label lblAvailableCourses;

    @FXML private ListView<Course> lvUserCourses;         // Назначенные курсы
    @FXML private ListView<Course> lvAvailableCourses;    // Доступные для записи

    private User user;
    private MainController mainController;

    private ObservableList<Course> allCourses;
    private ObservableList<Course> enrolledCourses;

    public void setUser(User user, MainController mainController) {
        this.user = user;
        this.mainController = mainController;

        boolean isAdmin = CurrentUser.isAdmin();
        boolean canEdit = isAdmin;

        // Основные поля
        lblId.setText(String.valueOf(user.getId()));
        tfName.setText(user.getName());
        tfEmail.setText(user.getEmail());
        tfAge.setText(String.valueOf(user.getAge()));

        tfName.setEditable(canEdit);
        tfEmail.setEditable(canEdit);
        tfAge.setEditable(canEdit);

        // Роль и пароль
        if (cbRole != null) {
            cbRole.setItems(FXCollections.observableArrayList("user", "admin"));
            cbRole.setValue(user.getRoleName());
            cbRole.setVisible(isAdmin);
            cbRole.setManaged(isAdmin);
            cbRole.setDisable(!isAdmin);
        }

        if (tfNewPassword != null) {
            tfNewPassword.setVisible(isAdmin);
            tfNewPassword.setManaged(isAdmin);
        }

        if (lblRole != null) {
            lblRole.setVisible(isAdmin);
            lblRole.setManaged(isAdmin);
        }
        if (lblPassword != null) {
            lblPassword.setVisible(isAdmin);
            lblPassword.setManaged(isAdmin);
        }

        if (btnSave != null) {
            btnSave.setVisible(canEdit);
            btnSave.setManaged(canEdit);
        }

        // === Курсы — только для админа ===
        if (isAdmin) {
            loadCoursesForUser();
            showCoursesSection(true);
        } else {
            showCoursesSection(false);
        }
    }

    private void showCoursesSection(boolean visible) {
        if (lblUserCourses != null) {
            lblUserCourses.setVisible(visible);
            lblUserCourses.setManaged(visible);
        }
        if (lblAvailableCourses != null) {
            lblAvailableCourses.setVisible(visible);
            lblAvailableCourses.setManaged(visible);
        }
        if (lvUserCourses != null) {
            lvUserCourses.setVisible(visible);
            lvUserCourses.setManaged(visible);
        }
        if (lvAvailableCourses != null) {
            lvAvailableCourses.setVisible(visible);
            lvAvailableCourses.setManaged(visible);
        }
    }

    private void loadCoursesForUser() {
        try {
            // Все курсы в системе
            allCourses = UserDAO.getAllCourses();

            // Курсы, на которые уже записан пользователь
            enrolledCourses = UserDAO.getEnrolledCourses(user.getId());

            // Назначенные курсы — напрямую
            lvUserCourses.setItems(enrolledCourses);

            // Доступные курсы — фильтруем через filtered list (лучший способ JavaFX)
            ObservableList<Course> availableCourses = allCourses.filtered(course ->
                    !enrolledCourses.contains(course)
            );

            lvAvailableCourses.setItems(availableCourses);

            // Кастомные ячейки
            lvUserCourses.setCellFactory(param -> new CourseCell());
            lvAvailableCourses.setCellFactory(param -> new CourseCell());

        } catch (SQLException e) {
            mainController.showAlert("Ошибка загрузки курсов: " + e.getMessage());
        }
    }

    @FXML
    private void enrollCourse() {
        if (!CurrentUser.isAdmin()) return;

        Course selected = lvAvailableCourses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainController.showAlert("Выберите курс для записи!");
            return;
        }

        try {
            UserDAO.enrollUserInCourse(user.getId(), selected.getId());
            // Обновляем списки
            enrolledCourses.add(selected);
            lvUserCourses.refresh();
            lvAvailableCourses.refresh();
        } catch (SQLException e) {
            mainController.showAlert("Ошибка записи на курс: " + e.getMessage());
        }
    }

    @FXML
    private void unenrollCourse() {
        if (!CurrentUser.isAdmin()) return;

        Course selected = lvUserCourses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainController.showAlert("Выберите курс для отписки!");
            return;
        }

        try {
            UserDAO.unenrollUserFromCourse(user.getId(), selected.getId());
            // Обновляем списки
            lvAvailableCourses.getItems().add(selected);
            enrolledCourses.remove(selected);
            lvUserCourses.refresh();
            lvAvailableCourses.refresh();
        } catch (SQLException e) {
            mainController.showAlert("Ошибка отписки от курса: " + e.getMessage());
        }
    }

    @FXML
    private void saveUser() {
        if (!CurrentUser.isAdmin()) {
            mainController.showAlert("У вас нет прав на редактирование!");
            return;
        }

        try {
            user.setName(tfName.getText().trim());
            user.setEmail(tfEmail.getText().trim());
            user.setAge(Integer.parseInt(tfAge.getText()));

            // Роль
            String selectedRole = cbRole.getValue();
            if (selectedRole != null) {
                user.setRoleId(selectedRole.equals("admin") ? 1 : 2);
                user.setRoleName(selectedRole);
            }

            // Новый пароль (если введён)
            String newPass = tfNewPassword.getText();
            if (!newPass.isEmpty()) {
                UserDAO.updatePassword(user.getId(), newPass);  // потом замени на хэш
            }

            UserDAO.updateUser(user);

            mainController.loadData();  // обновляем список пользователей в главном окне
            closeWindow();
        } catch (Exception e) {
            mainController.showAlert("Ошибка сохранения: " + e.getMessage());
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}