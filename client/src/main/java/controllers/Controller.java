package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.lang.reflect.Field;

public abstract class Controller {
    protected void translateLabel(Label label, String key, String ch) {
        //label.setText(ch.getCurrentBundle().getString(key));
    }

    protected void translateButton(Button button, String key, String ch) {
        //label.setText(ch.getCurrentBundle().getString(key));
    }

    protected Tooltip getTooltipWithDelay(String text, int ms) {
        Tooltip tooltip = new Tooltip(text);
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(ms)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tooltip;
    }
}