package ru.eltex.magnus.streamer;

import java.awt.*;
import java.io.*;
import java.net.Socket;


public class Streamer {

    private static final String PROPERTIES_FILE_PATH = "magnus.properties";

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Streamer() {
        PropertiesManager properties = new PropertiesManager();
        if (!properties.loadFromFile(PROPERTIES_FILE_PATH)) return;

        if (!connectToServer(properties.getServerAddress(), properties.getServerPort())) return;
        if (!signIn(properties.getLogin(), properties.getPassword())) return;
        listenToServer();
    }

    public static void main(String[] args){
        new Streamer();
    }

    private boolean connectToServer(String host, int port) {
        System.out.println("Trying to connect to server (" + host + ":" + port + ")");
        try {
            socket = new Socket(host, port);
            if (!socket.isConnected()) {
                System.out.println("Failed to connect");
                return false;
            }
            socket.setTcpNoDelay(true);
            System.out.println("Successfully connected");
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect");
            return false;
        }
    }

    private boolean signIn(String login, String password) {
        System.out.println("Trying to sign in");
        String authString = login + ":" + password;
        sendToServer(authString.getBytes());
        String answer = new String(readFromServer());
        System.out.println(answer);
        return answer.equals("verified");
    }

    private void listenToServer() {
        while (!socket.isClosed()) {
            String command = new String(readFromServer());
            switch (command) {
                case "screenshot": sendToServer(getScreenshot()); break;
                default: break;
            }
        }
    }

    public byte[] getScreenshot() {
        try {
            return new ScreenshotMaker().getScreenshot();
        } catch (IOException | AWTException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private void sendToServer(byte[] data) {
        try {
            outputStream.writeInt(data.length);
            outputStream.flush();
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFromServer() {
        try {
            int size = inputStream.readInt();
            byte[] data = new byte[size];
            inputStream.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}