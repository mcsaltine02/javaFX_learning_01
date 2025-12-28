package by.mcsaltine.javafx_crud;


import javafx.scene.control.ListCell;

public class UserCell extends ListCell<User> {
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if (empty || user == null) {
            setText(null);
        } else {
            setText(user.getId() + " — " + user.getName() + " (" + user.getRoleName() + ")");
        }
    }
}
