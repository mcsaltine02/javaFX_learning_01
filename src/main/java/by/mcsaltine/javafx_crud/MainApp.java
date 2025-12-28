package by.mcsaltine.javafx_crud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/by/mcsaltine/javafx_crud/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("Вход");
            stage.setScene(scene);
            stage.show();
        }

    public static void main(String[] args) {
        launch(args);
    }
}