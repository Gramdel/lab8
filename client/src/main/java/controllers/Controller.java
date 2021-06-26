package controllers;

import core.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

import static core.Main.getPrimaryStage;

public abstract class Controller {

    protected void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void changeScene(String name, String title) throws IOException {
        getPrimaryStage().setScene(new Scene(FXMLLoader.load(getClass().getResource(name))));
        getPrimaryStage().setTitle(title);
        getPrimaryStage().show();
    }

    protected void changeScene(String name) throws IOException {
        getPrimaryStage().setScene(new Scene(FXMLLoader.load(getClass().getResource(name))));
        getPrimaryStage().show();
    }

    protected void translateLabel(Label label, String key, String ch) {
        //label.setText(ch.getCurrentBundle().getString(key));
    }

    protected void translateButton(Button button, String key, String ch) {
        //label.setText(ch.getCurrentBundle().getString(key));
    }
}