package core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage primaryStage) {
        WindowManager.primaryStage = primaryStage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Scene getScene() {
        return primaryStage.getScene();
    }

    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void changeScene(String name, String title) throws IOException {
        primaryStage.setScene(new Scene(FXMLLoader.load(name.getClass().getResource("/" + name))));
        primaryStage.setTitle(title);
        primaryStage.show();
    }
}
