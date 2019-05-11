package ru.eltex.magnus.server;

import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.String;
import java.util.*;

public class StreamersServer {
    private static final int MAX_READ_BUFFER_SIZE = 2 << 10;

    private static final List<StreamerRequester> streamers = Collections.synchronizedList(new ArrayList<>());

    private static Thread thread;

    public static void start() {
        if (thread != null) {
            return;
        }
        thread = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(8081)) {
                System.out.println("Server Started");
                streamers.clear();

                Thread updateStreamersThread = new Thread(StreamersServer::updateOnlineStreamersList);
                updateStreamersThread.setDaemon(true);
                updateStreamersThread.start();

                while (!server.isClosed()) {
                    System.out.println("Waiting for incoming connection...");
                    Socket uncheckedStreamer = server.accept();
                    uncheckedStreamer.setTcpNoDelay(true);
                    System.out.println("New client connected: " + uncheckedStreamer.toString());

                    Thread signInThread = new Thread(() -> waitingForStreamerSignIn(uncheckedStreamer));
                    signInThread.setDaemon(true);
                    signInThread.start();
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
        return result.orElse(null);
    }

    private static void waitingForStreamerSignIn(Socket streamer) {
        try {
            DataInputStream inputStream = new DataInputStream(streamer.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(streamer.getOutputStream());

            byte[] bytes = readMessage(inputStream);
            if (bytes == null) {
                streamer.close();
                return;
            }

            String authString = new String(bytes);
            String[] authArray = authString.split(":");
            boolean verified = checkAuthData(authArray);

            String answer;
            if (verified) {
                String login = authArray[0];
                streamers.add(new StreamerRequester(streamer, inputStream, outputStream, login));
                answer = "verified";
                System.out.println("Authenticated successfully: " + login + " (" + streamer.toString() + ")");
            } else {
                answer = "failed";
                System.out.println("Authentication refused: " + streamer.toString());
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
                System.out.println("Checking up online streamers. " + streamers.size() +
                        " have been online by this moment");
                synchronized (streamers) {
                    Iterator<StreamerRequester> it = streamers.iterator();
                    while(it.hasNext()) {
                        StreamerRequester streamer = it.next();
                        if (!streamer.checkConnection()) {
                            System.out.println("Streamer disconnected: " + streamer.getLogin());
                            try {
                                streamer.close();
                            } catch (IOException e) {
                                System.out.println("Failed to close streamer connection");
                                e.printStackTrace();
                            }
                            it.remove();
                        }
                    }
                }
                System.out.println(streamers.size() + " streamers are still online");
                Thread.sleep(5000);
            }
        } catch (InterruptedException ignored) {
        }
    }
}