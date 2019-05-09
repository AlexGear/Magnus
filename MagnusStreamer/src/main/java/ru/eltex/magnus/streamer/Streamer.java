package ru.eltex.magnus.streamer;

import java.awt.*;
import java.io.*;
import java.net.Socket;


public class Streamer implements Runnable {

    private static Socket socket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;

    private static boolean stopWorking = false;

    @Override
    public void run() {
        if (!connectToServer()) {
            GUI.sendUserErrorMsg("Disconnected");
            return;
        }
        if (!signIn()) {
            GUI.sendUserErrorMsg("Bad login or password");
            return;
        }
        GUI.sendUserInformMsg("Connected");
        stopWorking = false;
        listenToServer();
    }

    private boolean connectToServer() {
        String host = App.properties.getServerAddress();
        int port = App.properties.getServerPort();

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

    private void reconnectToServer() {
        GUI.sendUserWarningMsg("Reconnecting");
        boolean connected = false;
        while (!connected){
            connected = connectToServer();
            if(!connected){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else GUI.sendUserInformMsg("Connected");
        }
        if(!signIn()) {
            GUI.sendUserErrorMsg("Bad login or password");
            disconnect();
        }
    }

    public static void disconnect() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            stopWorking = true;
            GUI.sendUserErrorMsg("Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean signIn() {
        String login = App.properties.getLogin();
        String password = App.properties.getPassword();

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
            if(stopWorking) return;
            switch (command) {
                case "screenshot": sendToServer(getScreenshot()); break;
                case "checkup": sendToServer("connected".getBytes()); break;
                case "": reconnectToServer(); break;
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