package ru.eltex.magnus.server.streamers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

import static ru.eltex.magnus.server.streamers.StreamDialog.*;


public class StreamerRequester implements Closeable {

    private static final int CONNECTION_CHECK_RETRIES = 3;
    private static final int SCREENSHOT_REFRESH_INTERVAL = 350;

    private static final Logger LOG = LogManager.getLogger(StreamerRequester.class);

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String login;

    private byte[] screenshot;
    private Thread thread;

    StreamerRequester(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, String login) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.login = login;
    }

    public void startStreaming() {
        if (thread != null) {
            return;
        }
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                takeScreenshot();
                try {
                    Thread.sleep(SCREENSHOT_REFRESH_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stopStreaming() {
        thread.interrupt();
    }

    public String getLogin() {
        return login;
    }

    public byte[] getScreenshot() {
        return screenshot;
    }

    private synchronized void takeScreenshot() {
        try {
            String command = "screenshot";
            sendMessage(outputStream, command.getBytes());
            screenshot = getMessage(inputStream);
        } catch (IOException e) {
            LOG.warn("Failed to take screenshot from '" + login + "': " + e.toString());
            screenshot = null;
        }
    }

    public void sendNotification(String text) {
        String command = "message";
        try {
            sendMessage(outputStream, command.getBytes());
            sendMessage(outputStream, text.getBytes());
        } catch (IOException e) {
            LOG.warn("Failed to send notification to '" + login + "': " + e.toString());
        }
    }

    public boolean checkConnection() {
        try {
            if (socket.isClosed()) {
                return false;
            }
            final String command = "checkup";
            for (int i = 0; i < CONNECTION_CHECK_RETRIES; i++) {
                sendMessage(outputStream, command.getBytes());
                byte[] bytes = getMessage(inputStream);
                if (bytes == null) {
                    LOG.warn("Connection check for '" + login + "' (retry #" + i + ") failed: bytes == null");
                    continue;
                }
                String response = new String(bytes);
                if ("connected".equals(response)) {
                    return true;
                }
                LOG.warn("Connection check for '" + login + "' (retry #" + i + ") failed: response = '" + response + "'");
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.warn("Failed to close streamer socket: " + e.toString());
        }
    }
}