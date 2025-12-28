package by.mcsaltine.javafx_crud;

import javafx.scene.control.ListCell;

public class CourseCell extends ListCell<Course> {
    @Override
    protected void updateItem(Course course, boolean empty) {
        super.updateItem(course, empty);

        if (empty || course == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Название жирным + описание ниже
            String text = course.getTitle() + "\n" + course.getDescription();
            setText(text);
            setStyle("-fx-font-size: 14px; -fx-padding: 10px;"); // для красоты и отступов
        }
    }
}