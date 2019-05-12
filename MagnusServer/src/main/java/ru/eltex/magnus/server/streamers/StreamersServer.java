package ru.eltex.magnus.server.streamers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.eltex.magnus.server.App;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;
import ru.eltex.magnus.server.db.storages.OfflineStreamersStorage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.String;
import java.util.*;

public class StreamersServer {
    private static final int MAX_READ_BUFFER_SIZE = 2 << 10;

    private static final Logger LOG = LogManager.getLogger(StreamersServer.class);
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Map<String, StreamerRequester> STREAMERS = Collections.synchronizedMap(new HashMap<>());
    private static Thread thread;

    private static StreamersServerProperties getProperties() {
        return App.PROPERTIES;
    }

    public static void start() {
        if (thread != null) {
            return;
        }
        thread = new Thread(() -> {
            int port = getProperties().getServerPort();
            LOG.info("Starting StreamersServer on port " + port);
            try (ServerSocket server = new ServerSocket(port)) {
                LOG.info("StreamersServer Started");
                STREAMERS.clear();

                Thread updateStreamersThread = new Thread(StreamersServer::onlineStreamersCheckupThreadProc);
                updateStreamersThread.setDaemon(true);
                updateStreamersThread.start();

                while (!server.isClosed()) {
                    LOG.info("Waiting for incoming connection...");

                    Socket uncheckedStreamer = server.accept();
                    uncheckedStreamer.setTcpNoDelay(true);
                    LOG.info("New client connected: " + uncheckedStreamer.toString());
                    
                    checkupOnlineStreamers();

                    Thread signInThread = new Thread(() -> waitingForStreamerSignIn(uncheckedStreamer));
                    signInThread.setDaemon(true);
                    signInThread.start();
                }

            } catch (IOException e) {
                LOG.error("StreamersServer thread stopped because of exception: " + e.toString());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static StreamerRequester getStreamerByLogin(String login) {
        return STREAMERS.get(login);
    }

    public static Collection<StreamerRequester> getAllStreamers() {
        return Collections.unmodifiableCollection(STREAMERS.values());
    }

    private static void waitingForStreamerSignIn(Socket socket) {
        try {
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            byte[] bytes = readMessage(inputStream);
            if (bytes == null) {
                socket.close();
                return;
            }

            String authString = new String(bytes);
            String[] authArray = authString.split(":");
            boolean verified = checkAuthData(authArray);

            String answer;
            if (verified) {
                String login = authArray[0];

                StreamerRequester streamer = new StreamerRequester(socket, inputStream, outputStream, login);
                StreamerRequester prev = STREAMERS.put(login, streamer);
                if(prev != null && prev.checkConnection()) {
                    // TODO: remote disconnection
                    answer = "failed";
                    LOG.info("Streamer " + login + " is already online. Connection refused.");
                }
                else {
                    if(prev == null) {
                        StoragesProvider.getOfflineStreamersStorage().removeOfflineStreamerByLogin(login);
                    }
                    answer = "verified";
                    LOG.info("Authenticated successfully: " + login + " (" + socket.toString() + ")");
                }
            } else {
                answer = "failed";
                LOG.info("Authentication refused: " + socket.toString());
            }

            outputStream.writeInt(answer.length());
            outputStream.write(answer.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOG.warn("Exception while waiting streamer to sign in: " + e.toString());
        }
    }

    private static byte[] readMessage(DataInputStream inputStream) throws IOException {
        int size = inputStream.readInt();
        if (size < 0 || size > MAX_READ_BUFFER_SIZE) {
            LOG.warn("Unacceptable message size: " + size);
            return null;
        }

        byte[] buffer = new byte[size];
        int actualSize = inputStream.read(buffer, 0, size);
        if (size != actualSize) {
            LOG.warn("Expected " + size + "bytes but got " + actualSize + ". Skipping this message");
            return null;
        }
        return buffer;
    }

    private static boolean checkAuthData(String[] authArray) {
        if (authArray.length != 2) return false;

        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();

        String login = authArray[0];
        String password = authArray[1];

        Employee employee = storage.getEmployeeByLogin(login);
        if (employee == null) return false;

        return PASSWORD_ENCODER.matches(password, employee.getPassword());
    }

    private static void onlineStreamersCheckupThreadProc() {
        try {
            while (true) {
                checkupOnlineStreamers();
                Thread.sleep(5000);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private static synchronized void checkupOnlineStreamers() {
        LOG.debug("Checking up online streamers. " + STREAMERS.size() +
                " have been online by this moment");
        List<StreamerRequester> disconnected = selectDisconnectedStreamers();
        for(StreamerRequester s : disconnected) {
            STREAMERS.remove(s.getLogin());
        }
        LOG.debug(STREAMERS.size() + " streamers are still online");

        OfflineStreamersStorage storage = StoragesProvider.getOfflineStreamersStorage();
        for(StreamerRequester streamer : disconnected) {
            LOG.info("Streamer disconnected: " + streamer.getLogin());
            streamer.close();
            OfflineStreamer offlineStreamer = OfflineStreamer.forCurrentTime(streamer.getLogin());
            storage.insertOfflineStreamer(offlineStreamer);
        }
    }

    private static List<StreamerRequester> selectDisconnectedStreamers() {
        List<StreamerRequester> streamersCopy = new ArrayList<>(STREAMERS.values());
        List<StreamerRequester> disconnected = new ArrayList<>();
        for(StreamerRequester streamer : streamersCopy) {
            if(!streamer.checkConnection()) {
                disconnected.add(streamer);
            }
        }
        return disconnected;
    }
}