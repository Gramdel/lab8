package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static core.WindowManager.*;

public class Main extends Application {
    private static Logger logger;
    private static String[] args;
    private static User user;
    private static InterpreterForClient interpreter;
    private static ResourceBundle currentBundle;


    public static void main(String[] args) {
        Main.args = args;
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            String loggerCfg = "handlers = java.util.logging.FileHandler\n" +
                    "java.util.logging.FileHandler.level     = ALL\n" +
                    "java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter\n" +
                    "java.util.logging.FileHandler.append    = true\n" +
                    "java.util.logging.FileHandler.pattern   = log.txt";
            LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(loggerCfg.getBytes()));
            logger = Logger.getLogger(Main.class.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args.length < 2) {
            logger.log(Level.WARNING, getStringFromBundle("cantStartErrorForLog"));
            showAlert(Alert.AlertType.ERROR, "ERROR", getStringFromBundle("cantStartAlertHeader"), getStringFromBundle("cantStartAlertContent"));
        } else {
            try {
                int port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                } else {
                    Client.setProperties(args[0], port);
                    Main.setCurrentBundle("en-CA");
                    Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/start.fxml")));
                    primaryStage.setTitle("PRODMAN: " + getStringFromBundle("startWindowTitle"));
                    primaryStage.setResizable(false);
                    primaryStage.setScene(scene);
                    setPrimaryStage(primaryStage);
                    primaryStage.show();
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, getStringFromBundle("wrongPortErrorForLog") + args[1] + "!");
                showAlert(Alert.AlertType.ERROR, "ERROR", getStringFromBundle("cantStartAlertHeader"), getStringFromBundle("wrongPortAlertContent"));
            }
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setUser(User user) {
        Main.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static void setInterpreter(InterpreterForClient interpreter) {
        Main.interpreter = interpreter;
    }

    public static InterpreterForClient getInterpreter() {
        return interpreter;
    }

    public static void setCurrentBundle(String tag) {
        Main.currentBundle = ResourceBundle.getBundle("bundles.Language", Locale.forLanguageTag(tag));
    }

    public static String getStringFromBundle(String key) {
        return new String(currentBundle.getString(key).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    public static String getCurrentBundleName() {
        return currentBundle.getLocale().getLanguage()+"-"+currentBundle.getLocale().getCountry();
    }

    public static ResourceBundle getCurrentBundle() {
        return currentBundle;
    }
}