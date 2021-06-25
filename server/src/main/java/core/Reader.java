package core;

import commands.Command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;

import static core.Main.getDBUnit;
import static core.Main.getLogger;

public class Reader extends Thread{
    private final DatagramSocket socket;
    private final DatagramPacket packet;
    private final byte[] b;

    public Reader(DatagramSocket socket, DatagramPacket packet, byte[] b) {
        this.socket = socket;
        this.packet = packet;
        this.b = b;
    }

    @Override
    public void run() {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Command command = (Command) objectInputStream.readObject();
            getLogger().log(Level.INFO, "Успешная десериализация команды " + command.getClass().getSimpleName() + "!");

            new Writer(socket, packet, command).start();
        } catch (IOException | ClassNotFoundException e) {
            getLogger().log(Level.WARNING,"Ошибка десериализации!");
        } catch (ClassCastException e) {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                User user = (User) objectInputStream.readObject();
                getLogger().log(Level.INFO, "Успешная десериализация пользователя с именем " + user.getName() + " и паролем " + user.getPassword() + "!");

                if (user.getCheckOrAdd()) {
                    user = getDBUnit().checkUser(user);
                } else {
                    user = getDBUnit().addUserToDB(user);
                }

                new Writer(socket, packet, user).start();
            } catch (IOException | ClassNotFoundException e1) {
                getLogger().log(Level.WARNING,"Ошибка десериализации!");
            }
        }
    }
}
