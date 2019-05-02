package ru.eltex.magnus.server;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class StreamerRequester {

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String login;

    StreamerRequester(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, String login){
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.login = login;
    }

    public String getLogin(){
        return  login;
    }

    public synchronized byte[] getScreenshot(){

            String command = "screenshot";
            sendToStreamer(command.getBytes());
            return readFromStreamer();
    }

    private void sendToStreamer(byte[] data){
        try {
            outputStream.writeInt(data.length);
            outputStream.flush();
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFromStreamer(){
        try {
            int size = inputStream.readInt();
            System.out.println(size);
            if(size < 0) return new byte[0];
            byte[] data = new byte[size];

            inputStream.read(data);

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
