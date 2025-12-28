package by.mcsaltine.javafx_crud;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CourseAdminCell extends ListCell<Course> {
    private final Button deleteButton = new Button("Удалить");

    public CourseAdminCell(MainController mainController) {
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px;");
        deleteButton.setOnAction(event -> {
            Course course = getItem();
            if (course != null) {
                mainController.deleteCourse(course.getId());
            }
        });
    }

    @Override
    protected void updateItem(Course course, boolean empty) {
        super.updateItem(course, empty);

        if (empty || course == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Название и описание
            Label titleLabel = new Label(course.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label descLabel = new Label(course.getDescription());
            descLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            descLabel.setWrapText(true);

            // Контейнер для текста
            VBox textBox = new VBox(5, titleLabel, descLabel);

            // Контейнер с текстом + кнопкой
            HBox hbox = new HBox(20, textBox, deleteButton);
            hbox.setAlignment(Pos.CENTER_LEFT);

            setGraphic(hbox);
            setText(null);
        }
    }
}