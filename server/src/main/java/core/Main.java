package core;

import collection.Organization;
import collection.Product;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static Logger logger;
    private static String url;
    private static String username;
    private static String password;
    private static int port;
    private static final LinkedHashSet<Product> collection = new LinkedHashSet<>();
    private static final ArrayList<Organization> organizations = new ArrayList<>();
    private static Date date;
    private static DBUnit dbUnit;
    private static Listener listener;

    public static void main(String[] args) {
        if (setConnectionDetails(args)) {
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

            date = new Date();
            dbUnit = new DBUnit(url, username, password, logger);
            try {
                dbUnit.connect();

                Scanner in = new Scanner(System.in);
                while (true) {
                    System.out.println("Введите пароль для входа в программу-сервер:");
                    int errorId = dbUnit.checkUser(new User("admin", in.nextLine())).getErrorId();
                    if (errorId == 0) {
                        System.out.println("Вы успешно вошли!");
                        break;
                    } else if (errorId == 1) {
                        System.out.println("Неправильный пароль!");
                    } else if (errorId == 2) {
                        System.out.println("Пользователя admin нет в базе данных! Запуск программы невозможен, программа завершает работу.");
                        System.exit(1);
                    } else if (errorId == 3) {
                        System.out.println("При проверке пароля возникла ошибка SQL!");
                    }
                    System.out.println("Для выхода введите exit (без пробелов), а для повторного ввода пароля - любую строку или нажмите Enter.");
                    if (in.nextLine().equals("exit")) {
                        System.out.println("Программа завершает работу.");
                        System.exit(1);
                    }
                }

                dbUnit.loadCollectionFromDB(collection, organizations);

                if (setPort(args)) {
                    if (!(listener = new Listener()).bind(port)) {
                        listener = null;
                    }
                }
                System.out.println("Вас приветствует программа-сервер для управления коллекцией продуктов. Для получения списка команд введите help. \n" + "Введите команду:");
                if (listener != null) {
                    listener.start();
                }

                InterpreterForServer interpreter = new InterpreterForServer(collection, organizations, date, dbUnit, new User("admin"));
                interpreter.fromStream(System.in, true);
            } catch (SQLException e) {
                System.out.println("Возникла ошибка: " + e.getMessage());
                System.out.println("Программа завершает работу, т.к. нет подключения к базе данных!");
            }
        } else {
            System.out.println("Программа не запущена из-за указанной выше ошибки!");
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean setConnectionDetails(String[] args) {
        if (args.length > 0) {
            if (!Files.exists(Paths.get(args[0]))) {
                System.out.println("Файл для подключения к базе данных с именем " + args[0] + " не существует!");
            } else if (Files.isDirectory(Paths.get(args[0]))) {
                System.out.println("Файл для подключения к базе данных с именем " + args[0] + " не может быть открыт - в качестве файла была передана директория!");
            } else if (!Files.isRegularFile(Paths.get(args[0]))) {
                System.out.println("Файл для подключения к базе данных с именем " + args[0] + " не может быть открыт -  был передан специальный файл!");
            } else if (!Files.isReadable(Paths.get(args[0]))) {
                System.out.println("Файл для подключения к базе данных с именем " + args[0] + " не может быть открыт - нет прав на чтение!");
            } else {
                try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
                    String s;
                    if ((s = in.readLine()) != null) {
                        url = s;
                        if ((s = in.readLine()) != null) {
                            username = s;
                            if ((s = in.readLine()) != null) {
                                password = s;
                                return true;
                            } else {
                                System.out.println("В файле для подключения к базе данных с именем " + args[0] + " отсутсвует password!");
                            }
                        } else {
                            System.out.println("В файле для подключения к базе данных с именем " + args[0] + " отсутсвуют username и password!");
                        }
                    } else {
                        System.out.println("Файл для подключения к базе данных с именем " + args[0] + " пуст!");
                    }
                } catch (IOException e) {
                    System.out.println("Файл для подключения к базе данных с именем " + args[0] + " не существует!");
                }
            }
        }
        return false;
    }

    public static boolean setPort(String[] args) {
        if (args.length >= 2) {
            System.out.println("Пытаемся запустить сервер на порте " + args[1] + "...");
            try {
                int port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                } else {
                    Main.port = port;
                }
                return true;
            } catch (NumberFormatException e) {
                System.out.println("Сервер не запущен, так как указан неправильный формат порта!\n(число от 1 до 65535 должно быть передано вторым аргументом командной строки)");
                logger.log(Level.WARNING, "Не удалось запустить сервер из-за неправильного формата порта: " + args[1] + "!");
            }
        } else {
            System.out.println("Сервер не запущен, так как не указан порт!\n(число от 1 до 65535 должно быть передано вторым аргументом командной строки)");
            logger.log(Level.WARNING, "Сервер не запущен, так как не указан порт!");
        }
        return false;
    }

    public static LinkedHashSet<Product> getCollection() {
        return collection;
    }

    public static ArrayList<Organization> getOrganizations() {
        return organizations;
    }

    public static Date getDate() {
        return date;
    }

    public static DBUnit getDBUnit() {
        return dbUnit;
    }
}