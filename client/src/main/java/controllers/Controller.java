package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public abstract class Controller {
    protected void translateLabel(Label label, String key, String ch) {
        //label.setText(ch.getCurrentBundle().getString(key));
    }

    protected void translateButton(Button button, String key, String ch) {
        //label.setText(ch.getCurrentBundle().getString(key));
    }
}