package ru.eltex.magnus.server.streamers;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class StreamerRequester implements Closeable {

    private static final int MAX_BUFFER_SIZE = 2 << 18;
    private static final int CONNECTION_CHECK_RETRIES = 3;

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
            byte[] bytes = readFromStreamer();
            return bytes != null ? bytes : new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
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
                if (bytes != null && "connected".equals(new String(bytes))) {
                    return true;
                }
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

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close streamer socket");
            e.printStackTrace();
        }
    }
}