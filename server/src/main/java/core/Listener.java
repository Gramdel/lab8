package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static core.Main.getLogger;

public class Listener extends Thread {
    private DatagramSocket socket;

    public boolean bind (int port) {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Сервер успешно запущен!");
            getLogger().log(Level.INFO, "Сервер успешно запущен!");
            return true;
        } catch (SocketException e) {
            System.out.println("Не удалось запустить сервер на порте " + port + "!");
            getLogger().log(Level.WARNING, "Не удалось запустить сервер на порте " + port + "!");
            return false;
        }
    }

    @Override
    public void run() {
        ExecutorService es = Executors.newFixedThreadPool(10);
        while (true) {
            byte[] b = new byte[10000];
            DatagramPacket packet = new DatagramPacket(b, b.length);
            try {
                socket.receive(packet);
                getLogger().log(Level.INFO, "Получен пакет от клиента " + packet.getAddress().toString().substring(1));
                es.execute(new Reader(socket, packet, b));
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "При получении пакета возникла ошибка I/O: " + e.getMessage());
            }
        }
    }
}
