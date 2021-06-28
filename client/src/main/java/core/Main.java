package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static core.WindowManager.setPrimaryStage;
import static core.WindowManager.showAlert;

public class Main extends Application {
    private static Logger logger;
    private static String[] args;
    private static User user;
    private static InterpreterForClient interpreter;

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
            System.out.println("Не удалось инициализировать логгер!");
        }

        if (args.length < 2) {
            logger.log(Level.WARNING, "Не удалось запустить программу, т.к. не переданы IP и порт!");
            showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка запуска программы", "Программа не запущена, так как не переданы IP (или hostname) и порт сервера!\n(Они должны быть переданы через аргументы командной строки. Формат IP: xxx.xxx.xxx.xxx; формат hostname: непустая строка; формат порта: число от 1 до 65535.)");
        } else {
            try {
                int port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                } else {
                    Client.setProperties(args[0], port);

                    Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/start.fxml")));

                    primaryStage.setResizable(false);
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("PRODMAN: Авторизация");
                    setPrimaryStage(primaryStage);
                    primaryStage.show();

                    //Client.settUser();

                    //System.out.println("Для получения списка команд введите help. \n" + "Введите команду:");
                    //InterpreterForClient interpreter = new InterpreterForClient(user);
                    //interpreter.fromStream(System.in, true);
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Не удалось запустить программу из-за неправильного формата порта: " + args[1] + "!");
                showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка запуска программы", "Программа не запущена, так как указан неправильный формат порта!\n(число от 1 до 65535 должно быть передано вторым аргументом командной строки)");
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
}