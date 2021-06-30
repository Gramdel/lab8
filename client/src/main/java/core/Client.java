package core;

import commands.Command;

import java.io.*;
import java.net.*;
import java.util.logging.Level;

import static core.Main.getLogger;

public class Client {
    private static String hostname;
    private static int port;
    private static String content;

    public static void setProperties(String hostname, int port) {
        Client.hostname = hostname;
        Client.port = port;
    }

    public static synchronized String sendCommandAndReceiveResult(Command command) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
            byte[] b = byteArrayOutputStream.toByteArray();

            try {
                SocketAddress address = new InetSocketAddress(hostname, port);
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(500);
                DatagramPacket packet = new DatagramPacket(b, b.length, address);
                socket.send(packet);
                getLogger().log(Level.INFO, "Команда " + command.getClass().getSimpleName() + " успешно отправлена на сервер " + hostname + ":" + port + "!");

                try {
                    b = new byte[10000];
                    packet = new DatagramPacket(b, b.length);
                    socket.receive(packet);
                    getLogger().log(Level.INFO, "Ответ от сервера успешно получен!");
                    return new String(b).trim();
                } catch (SocketTimeoutException e) {
                    content = "Время ожидания ответа от сервера истекло!";
                    getLogger().log(Level.WARNING, "Время ожидания ответа от сервера истекло!");
                }
            } catch (SocketException e) {
                content = "Ошибка отправки пакета!";
                getLogger().log(Level.WARNING, "Ошибка отправки пакета!");
            } catch (IllegalArgumentException e) {
                content = "Не удаётся подключиться к серверу " + hostname + ":" + port + "! Проверьте данные для подключения.";
                getLogger().log(Level.WARNING, "Не удаётся подключиться к серверу " + hostname + ":" + port + "!");
            }
        } catch (IOException e) {
            content = "Ошибка сериализации!";
            getLogger().log(Level.WARNING, "Ошибка сериализации!");
        }
        return null;
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
                socket.setSoTimeout(150);
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
                        return user;
                    } catch (ClassNotFoundException e) {
                        content = "Ошибка десериализации!";
                        getLogger().log(Level.WARNING, "Ошибка десериализации пользователя!");
                        return null;
                    }
                } catch (SocketTimeoutException e) {
                    content = "Время ожидания ответа от сервера истекло!";
                    getLogger().log(Level.WARNING, "Время ожидания ответа о пользователе от сервера истекло!");
                    return null;
                }
            } catch (SocketException e) {
                content = "Ошибка отправки пакета!";
                getLogger().log(Level.WARNING, "Ошибка отправки пакета о пользователе!");
                return null;
            } catch (IllegalArgumentException e) {
                content = "Не удаётся подключиться к серверу " + hostname + ":" + port + "!";
                getLogger().log(Level.WARNING, "Не удаётся подключиться к серверу " + hostname + ":" + port + "!");
                return null;
            }
        } catch (IOException e) {
            content = "Ошибка сериализации!";
            getLogger().log(Level.WARNING, "Ошибка сериализации пользователя!");
            return null;
        }
    }

    public static String getContent() {
        return content;
    }
}