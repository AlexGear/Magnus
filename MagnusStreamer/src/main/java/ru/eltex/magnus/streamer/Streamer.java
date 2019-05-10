package ru.eltex.magnus.streamer;

import java.io.*;
import java.net.Socket;


public class Streamer {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private Thread thread;

    public void init() {
        if(thread != null) {
            return;
        }
        startThread();
    }

    private void startThread() {
        thread = new Thread(this::threadProc);
        thread.setDaemon(true);
        thread.start();
    }

    public void onPropertiesUpdated() {
        if(thread == null) {
            System.out.println("Streamer is not initialized");
            return;
        }
        thread.interrupt();
        startThread();
    }

    private void threadProc() {
        boolean connectFailedLastTime = false;
        boolean signInFailedLastTime = false;
        try {
            while (true) {
                if (!connectToServer()) {
                    if (!connectFailedLastTime) {
                        GUI.sendUserErrorMsg("Disconnected");
                    }
                    connectFailedLastTime = true;
                    Thread.sleep(5000);
                    continue;
                }
                connectFailedLastTime = false;

                if (!signIn()) {
                    if (!signInFailedLastTime) {
                        GUI.sendUserErrorMsg("Bad login or password");
                    }
                    signInFailedLastTime = true;
                    Thread.sleep(5000);
                    continue;
                }
                signInFailedLastTime = false;

                GUI.sendUserInformMsg("Connected");
                listenToServer();
            }
        } catch(InterruptedException ignored) {
        }
    }

    private boolean connectToServer() {
        String host = App.PROPERTIES.getServerAddress();
        int port = App.PROPERTIES.getServerPort();

        System.out.println("Trying to connect to server (" + host + ":" + port + ")");
        try {
            socket = new Socket(host, port);
            if (!socket.isConnected()) {
                System.out.println("Failed to connect");
                socket.close();
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

//    private void reconnectToServer() {
//        GUI.sendUserWarningMsg("Reconnecting");
//        boolean connected = false;
//        while (!connected) {
//            connected = connectToServer();
//            if (!connected) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            } else GUI.sendUserInformMsg("Connected");
//        }
//        if (!signIn()) {
//            GUI.sendUserErrorMsg("Bad login or password");
//            disconnect();
//        }
//    }

    public void disconnect() {
        try {
            socket.close();
            GUI.sendUserErrorMsg("Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean signIn() {
        String login = App.PROPERTIES.getLogin();
        String password = App.PROPERTIES.getPassword();

        System.out.println("Trying to sign in");
        String authString = login + ":" + password;
        sendToServer(authString.getBytes());

        byte[] bytes = readFromServer();
        if(bytes == null)
            return false;

        String answer = new String(bytes);
        System.out.println(answer);
        return answer.equals("verified");
    }

    private void listenToServer() {
        while (!socket.isClosed()) {
            byte[] bytes = readFromServer();
            if(bytes == null)
                return;

            String command = new String(bytes);
            switch (command) {
                case "screenshot":
                    sendToServer(takeScreenshot());
                    break;
                case "checkup":
                    sendToServer("connected".getBytes());
                    break;
//                case "":
//                    reconnectToServer();
//                    break;
            }
        }
    }

    private byte[] takeScreenshot() {
        try {
            return ScreenshotMaker.takeScreenshot();
        } catch (IOException e) {
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
            inputStream.read(data, 0, size);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}