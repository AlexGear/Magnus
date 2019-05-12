package ru.eltex.magnus.streamer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;


public class Streamer {
    private static final Logger LOG = LogManager.getLogger(Streamer.class);

    private enum SignInResult { VERIFIED, FAILED, OCCUPIED }

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private Thread thread;
    private volatile boolean lastConnectResult;
    private volatile SignInResult lastSignInResult;

    public void init() {
        if(thread != null) {
            return;
        }
        startThread();
    }

    public void onPropertiesUpdated() {
        if(thread == null) {
            LOG.warn("Streamer is not initialized");
            return;
        }
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                LOG.warn("Failed to close socket: " + e.toString());
            }
        }
        makeGUIMessagesShowNextTime();
    }

    private void makeGUIMessagesShowNextTime() {
        lastConnectResult = true;
        lastSignInResult = SignInResult.VERIFIED;
    }

    private void startThread() {
        thread = new Thread(this::threadProc);
        thread.setDaemon(true);
        thread.start();
    }

    private void threadProc() {
        LOG.info("Streamer thread started");

        makeGUIMessagesShowNextTime();
        try {
            while (true) {
                if (!connectToServer()) {
                    if (lastConnectResult) {
                        GUI.sendUserErrorMsg("Disconnected");
                    }
                    lastConnectResult = false;
                    Thread.sleep(5000);
                    continue;
                }
                lastConnectResult = true;

                SignInResult signInResult = signIn();
                if (SignInResult.VERIFIED != signInResult) {
                    if (lastSignInResult != signInResult) {
                        switch(signInResult) {
                            case FAILED: GUI.sendUserErrorMsg("Bad login or password"); break;
                            case OCCUPIED: GUI.sendUserErrorMsg("This user is already signed in"); break;
                        }
                        lastSignInResult = signInResult;
                    }
                    Thread.sleep(5000);
                    continue;
                }
                lastSignInResult = signInResult;

                GUI.sendUserInformMsg("Connected");
                listenToServer();
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean connectToServer() {
        String host = App.PROPERTIES.getServerAddress();
        int port = App.PROPERTIES.getServerPort();

       LOG.debug("Trying to connect to server (" + host + ":" + port + ")");
        try {
            try {
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                LOG.warn("Failed to close previous connection: " + e.toString());
            }

            socket = new Socket(host, port);
            if (!socket.isConnected()) {
                LOG.debug("Failed to connect");
                socket.close();
                return false;
            }
            socket.setTcpNoDelay(true);
            LOG.info("Successfully connected");
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            return true;
        } catch (IOException e) {
            LOG.debug("Failed to connect: " + e.toString());
            return false;
        }
    }

    public void disconnect() {
        try {
            socket.close();
            GUI.sendUserErrorMsg("Disconnected");
        } catch (IOException e) {
            LOG.warn("Failed to disconnect:" + e.toString());
        }
    }

    private SignInResult signIn() {
        String login = App.PROPERTIES.getLogin();
        String password = App.PROPERTIES.getPassword();

        LOG.info("Trying to sign in");
        String authString = login + ":" + password;
        sendToServer(authString.getBytes());

        byte[] bytes = readFromServer();
        if(bytes == null) {
            LOG.warn("Failed to sign in: response bytes == null");
            return SignInResult.FAILED;
        }

        String answer = new String(bytes);
        LOG.info("Signing in: answer = '" + answer + "'");
        switch(answer) {
            case "verified": return SignInResult.VERIFIED;
            case "occupied": return SignInResult.OCCUPIED;
            default: return SignInResult.FAILED;
        }
    }

    private void listenToServer() {
        while (!socket.isClosed()) {
            byte[] bytes = readFromServer();
            if(bytes == null) {
                LOG.warn("Listening to server: bytes == null");
                return;
            }

            String command = new String(bytes);
            switch (command) {
                case "screenshot":
                    sendToServer(takeScreenshot());
                    break;
                case "checkup":
                    sendToServer("connected".getBytes());
                    break;
            }
        }
    }

    private byte[] takeScreenshot() {
        try {
            return ScreenshotMaker.takeScreenshot();
        } catch (IOException e) {
            LOG.warn("Failed to take screenshot: " + e.toString());
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
            LOG.warn("Failed to send data (" + data.length + " bytes) to server: " + e.toString());
        }
    }

    private byte[] readFromServer() {
        try {
            int size = inputStream.readInt();
            byte[] data = new byte[size];
            inputStream.readFully(data, 0, size);
            return data;
        } catch (IOException e) {
            LOG.warn("Failed to read from server: " + e.toString());
            return null;
        }
    }
}