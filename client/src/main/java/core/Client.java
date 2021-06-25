package core;

import commands.Command;

import java.io.*;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;

import static core.Main.getLogger;

public class Client {
    private static String hostname;
    private static int port;
    private static User user;

    public static void setProperties(String hostname, int port) {
        Client.hostname = hostname;
        Client.port = port;
    }

    public static void setUser() {
        System.out.println("Вас приветствует программа-клиент для управления коллекцией продуктов!");
        Scanner in = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Для входа введите 1, для регистрации введите 2, для выхода введите 3 (без пробелов):");
                String check = in.nextLine();
                if (!check.equals("1") & !check.equals("2") & !check.equals("3")) {
                    throw new InputMismatchException();
                } else if (check.equals("3")){
                    System.out.println("Программа завершает работу.");
                    System.exit(1);
                } else {
                    System.out.println("Введите имя пользователя:");
                    String name = in.nextLine();
                    System.out.println("Введите пароль: ");
                    String password = in.nextLine();
                    User user = new User(name, password);
                    user.setCheckOrAdd(check.equals("1"));
                    user = sendAndReceiveUser(user);
                    if (user != null) {
                        Client.user = user;
                        if (check.equals("1")) {
                            System.out.print("Вы успешно вошли! ");
                        } else {
                            System.out.print("Вы успешно зарегистрировались! ");
                        }
                        break;
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Некорректный ввод!");
            }
        }
    }

    public static void sendCommandAndReceiveResult(Command command) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
            byte[] b = byteArrayOutputStream.toByteArray();

            try {
                SocketAddress address = new InetSocketAddress(hostname, port);
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(5000);
                DatagramPacket packet = new DatagramPacket(b, b.length, address);
                socket.send(packet);
                getLogger().log(Level.INFO, "Команда " + command.getClass().getSimpleName() + " успешно отправлена на сервер " + hostname + ":" + port + "!");

                try {
                    b = new byte[10000];
                    packet = new DatagramPacket(b, b.length);
                    socket.receive(packet);
                    getLogger().log(Level.INFO, "Ответ от сервера успешно получен!");
                    System.out.println(new String(b).trim());
                } catch (SocketTimeoutException e) {
                    System.out.println("Время ожидания ответа от сервера истекло!");
                    getLogger().log(Level.WARNING, "Время ожидания ответа от сервера истекло!");
                }
            } catch (SocketException e) {
                System.out.println("Ошибка отправки пакета!");
                getLogger().log(Level.WARNING, "Ошибка отправки пакета!");
            } catch (IllegalArgumentException e) {
                System.out.println("Не удаётся подключиться к серверу " + hostname + ":" + port + "! Проверьте данные для подключения.");
                getLogger().log(Level.WARNING, "Не удаётся подключиться к серверу " + hostname + ":" + port + "!");
            }
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Ошибка сериализации!");
        }
    }

    public static User sendAndReceiveUser(User user) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            byte[] b = byteArrayOutputStream.toByteArray();

            try {
                SocketAddress address = new InetSocketAddress(hostname, port);
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(5000);
                DatagramPacket packet = new DatagramPacket(b, b.length, address);
                socket.send(packet);
                getLogger().log(Level.INFO, "Пользователь успешно отправлен на сервер " + hostname + ":" + port + "!");

                try {
                    b = new byte[10000];
                    packet = new DatagramPacket(b, b.length);
                    socket.receive(packet);
                    getLogger().log(Level.INFO, "Ответ о пользователе от сервера успешно получен!");
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    try {
                        user = (User) objectInputStream.readObject();
                        getLogger().log(Level.INFO, "Успешная десериализация пользователя с именем " + user.getName() + " и паролем " + user.getPassword() + "!");
                        switch (user.getErrorId()) {
                            case 0:
                                return user;
                            case 1:
                                System.out.println("Неправильный пароль! Повторите ввод.");
                                break;
                            case 2:
                                System.out.println("Пользователя с таким именем и паролем не существует! Повторите ввод.");
                                break;
                            case 3:
                                System.out.println("При проверке/создании имени пользователя и пароля возникла ошибка SQL! Повторите ввод.");
                                break;
                            case 4:
                                System.out.println("Пользователь с таким именем уже зарегистрирован! Повторите ввод.");
                                break;
                        }
                        return null;
                    } catch (ClassNotFoundException e) {
                        System.out.println("Ошибка десериализации пользователя! Повторите ввод.");
                        getLogger().log(Level.WARNING, "Ошибка десериализации пользователя!");
                        return null;
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Время ожидания ответа о пользователе от сервера истекло! Повторите ввод.");
                    getLogger().log(Level.WARNING, "Время ожидания ответа о пользователе от сервера истекло!");
                    return null;
                }
            } catch (SocketException e) {
                System.out.println("Ошибка отправки пакета о пользователе! Повторите ввод.");
                getLogger().log(Level.WARNING, "Ошибка отправки пакета о пользователе!");
                return null;
            } catch (IllegalArgumentException e) {
                System.out.println("Не удаётся подключиться к серверу " + hostname + ":" + port + "! Проверьте данные для подключения, повторите ввод.");
                getLogger().log(Level.WARNING, "Не удаётся подключиться к серверу " + hostname + ":" + port + "!");
                return null;
            }
        } catch (IOException e) {
            System.out.println("Ошибка сериализации пользователя! Повторите ввод.");
            getLogger().log(Level.WARNING, "Ошибка сериализации пользователя!");
            return null;
        }
    }

    public static User getUser() {
        return user;
    }
}