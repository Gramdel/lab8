package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static Logger logger;

    public static void main(String[] args) {
        Application.launch();
        if (args.length<2) {
            System.out.println("Программа не запущена, так как не переданы IP (или hostname) и порт сервера!\n(Они должны быть переданы через аргументы командной строки. Формат IP: xxx.xxx.xxx.xxx; формат hostname: непустая строка; формат порта: число от 1 до 65535.)");
        } else {
            try {
                int port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                } else {
                    try {
                        String loggerCfg = "handlers = java.util.logging.FileHandler\n" +
                                "java.util.logging.FileHandler.level     = ALL\n" +
                                "java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter\n" +
                                "java.util.logging.FileHandler.append    = true\n" +
                                "java.util.logging.FileHandler.pattern   = log.txt";
                        LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(loggerCfg.getBytes()));
                        logger = Logger.getLogger(Main.class.getName());
                        System.out.println("Логгер успешно инициализирован!");
                    } catch (IOException e) {
                        System.out.println("Не удалось инициализировать логгер!");
                    }

                    Client.setProperties(args[0], port);
                    Client.setUser();

                    System.out.println("Для получения списка команд введите help. \n" + "Введите команду:");
                    InterpreterForClient interpreter = new InterpreterForClient(Client.getUser());
                    interpreter.fromStream(System.in, true);
                }
            } catch (NumberFormatException e) {
                System.out.println("Программа не запущена, так как указан неправильный формат порта!\n(число от 1 до 65535 должно быть передано вторым аргументом командной строки)");
                logger.log(Level.WARNING,"Не удалось запустить программу из-за неправильного формата порта: "+args[1]+"!");
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("PRODMAN");
        primaryStage.show();
    }

    public static Logger getLogger() {
        return logger;
    }
}