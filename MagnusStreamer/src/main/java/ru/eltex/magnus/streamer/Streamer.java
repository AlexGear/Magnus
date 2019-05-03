package ru.eltex.magnus.streamer;

import ru.eltex.magnus.streamer.ScreenshotMaker;

import java.awt.*;
import java.io.*;
import java.lang.String;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Streamer {

    public Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private String login = "maxim";
    private String password = "qwerty";

    private void connectToServer(){
        try {
            socket = new Socket("localhost", 8081);
            try {
                socket.setTcpNoDelay(true);
                System.out.println("Connected");
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                String authString = login + ":" + password;
                sendToServ(authString.getBytes());

                String answer = new String(readFromServ());
                System.out.println(answer);
                if (!answer.equals("verified")) return;

                byte[] data;
                while (socket.isConnected()) {
                    System.out.println("Whaiting..");

                    data = readFromServ();
                    String command = new String(data);
                    System.out.println(command);

                    switch (command){
                        case "screenshot" : sendToServ(getScreenshot()); break;
                        default: break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public byte[] getScreenshot(){
        try {
            byte[] screenshot = new ScreenshotMaker().getScreenshot();
            return screenshot;
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private void sendToServ(byte[] data){
        try {
            System.out.println("Array size " + data.length);
            outputStream.writeInt(data.length);
            outputStream.flush();
            outputStream.write(data);
            outputStream.flush();;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFromServ(){
        try {
            int size = inputStream.readInt();
            byte[] data = new byte[size];
            inputStream.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public Streamer(){
        connectToServer();
    }

    public static void main(String[] args){
        new Streamer();
    }

}