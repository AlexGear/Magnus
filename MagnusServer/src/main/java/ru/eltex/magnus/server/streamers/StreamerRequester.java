package ru.eltex.magnus.server.streamers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class StreamerRequester implements Closeable {

    private static final int MAX_BUFFER_SIZE = 2 << 18;
    private static final int CONNECTION_CHECK_RETRIES = 3;

    private static final Logger LOG = LogManager.getLogger(StreamerRequester.class);

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String login;

    StreamerRequester(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, String login) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public synchronized byte[] takeScreenshot() {
        try {
            String command = "screenshot";
            sendToStreamer(command.getBytes());
            return readFromStreamer();
        } catch (IOException e) {
            LOG.warn("Failed to take screenshot from '" + login + "': " + e.toString());
            return null;
        }
    }

    public boolean checkConnection() {
        try {
            if (socket.isClosed()) {
                return false;
            }
            final String command = "checkup";
            for (int i = 0; i < CONNECTION_CHECK_RETRIES; i++) {
                sendToStreamer(command.getBytes());
                byte[] bytes = readFromStreamer();
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

    private synchronized void sendToStreamer(byte[] data) throws IOException {
        outputStream.writeInt(data.length);
        outputStream.write(data);
        outputStream.flush();
    }

    private synchronized byte[] readFromStreamer() throws IOException {
        int size = inputStream.readInt();
        if (size < 0 || size > MAX_BUFFER_SIZE) {
            LOG.warn("Unacceptable message size: " + size);
            return null;
        }

        byte[] buffer = new byte[size];
        inputStream.readFully(buffer, 0, size);
        return buffer;
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