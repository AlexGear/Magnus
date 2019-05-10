package ru.eltex.magnus.server;

import ru.eltex.magnus.server.db.Database;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.String;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class StreamersServer {
    private static final int MAX_READ_BUFFER_SIZE = 2 << 10;

    private static ArrayList<StreamerRequester> streamers;

    private static Thread thread;

    public static void start() {
        if (thread != null) {
            return;
        }
        thread = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(8081)) {
                System.out.println("Server Started");
                streamers = new ArrayList<>();

                Thread updateStreamersThread = new Thread(StreamersServer::updateOnlineStreamersList);
                updateStreamersThread.setDaemon(true);
                updateStreamersThread.start();

                while (!server.isClosed()) {
                    System.out.println("Waiting for new client");
                    Socket uncheckedStreamer = server.accept();
                    uncheckedStreamer.setTcpNoDelay(true);
                    System.out.println("New Client Connected");

                    Thread signUpThread = new Thread(() -> waitingForStreamerSignUp(uncheckedStreamer));
                    signUpThread.setDaemon(true);
                    signUpThread.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static StreamerRequester getStreamerReqByLogin(String login) {
        Optional<StreamerRequester> result = streamers.stream().filter(x -> x.getLogin().equals(login)).findFirst();
        if (!result.isPresent()) {
            return null;
        }
        return result.get();
    }

    private static void waitingForStreamerSignUp(Socket streamer) {
        try {
            DataInputStream inputStream = new DataInputStream(streamer.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(streamer.getOutputStream());

            byte[] bytes = readMessage(inputStream);
            if (bytes == null) return;

            String authString = new String(bytes);
            String[] authArray = authString.split(":");
            boolean verified = checkAuthData(authArray);
            System.out.println("Received auth data: " + authArray[0] + " " + authArray[1]);

            String answer;
            if (verified) {
                streamers.add(new StreamerRequester(streamer, inputStream, outputStream, authArray[0]));
                answer = "verified";
            } else {
                answer = "failed";
            }
            outputStream.writeInt(answer.length());
            outputStream.write(answer.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readMessage(DataInputStream inputStream) throws IOException {
        int size = inputStream.readInt();
        if (size < 0 || size > MAX_READ_BUFFER_SIZE) {
            System.err.println("Unacceptable message size: " + size);
            return null;
        }

        byte[] buffer = new byte[size];
        int actualSize = inputStream.read(buffer, 0, size);
        if (size != actualSize) {
            System.err.println("Expected " + size + "bytes but got " + actualSize);
            return null;
        }
        return buffer;
    }

    private static boolean checkAuthData(String[] authArray) {
        if (authArray.length != 2) return false;
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        Employee employee = storage.getEmployeeByLogin(authArray[0]);
        if (employee == null) return false;
        return employee.getPassword().equals(authArray[1]);
    }

    private static void updateOnlineStreamersList() {
        try {
            while (true) {
                streamers.removeIf(s -> !s.checkStreamerConnection());
                Thread.sleep(5000);
            }
        } catch (InterruptedException ignored) {
        }
    }
}