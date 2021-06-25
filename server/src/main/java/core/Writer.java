package core;

import commands.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;

import static core.Main.*;

public class Writer extends Thread {
    private final DatagramSocket socket;
    private DatagramPacket packet;
    private Command command;
    private User user;

    public Writer(DatagramSocket socket, DatagramPacket packet, Command command) {
        this.socket = socket;
        this.packet = packet;
        this.command = command;
    }

    public Writer(DatagramSocket socket, DatagramPacket packet, User user) {
        this.socket = socket;
        this.packet = packet;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            byte[] b;
            if (command != null) {
                String response = command.execute(getCollection(), getOrganizations(), getDate(), getDBUnit());
                b = response.getBytes();
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
                b = byteArrayOutputStream.toByteArray();
            }
            packet = new DatagramPacket(b, b.length, packet.getAddress(), packet.getPort());
            socket.send(packet);
            getLogger().log(Level.INFO, "Ответ для клиента " + packet.getAddress().toString().substring(1) + " успешно отправлен!");
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Ошибка отправки ответа клиенту " + packet.getAddress().toString().substring(1) + "!");
        }
    }
}
