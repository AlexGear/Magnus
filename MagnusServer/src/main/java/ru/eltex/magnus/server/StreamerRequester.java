package ru.eltex.magnus.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
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

        try {
            String command = "screenshot";
            sendToStreamer(command.getBytes());
            return readFromStreamer();
        }catch (IOException e){
            e.printStackTrace();
            return new byte[0];
        }
    }

    public boolean checkStreamerConnection(){
        try {
            if(socket.isClosed()) return true;
            String command = "checkup";
            sendToStreamer(command.getBytes());
            String answer = new String(readFromStreamer());
            return answer != "";
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("catched");
            return false;
        }
    }

    private void sendToStreamer(byte[] data) throws IOException {
            outputStream.writeInt(data.length);
            outputStream.flush();
            outputStream.write(data);
            outputStream.flush();
    }

    private synchronized byte[] readFromStreamer() throws IOException{
            int size = inputStream.readInt();
            System.out.println(size);
            if(size <= 0 || size > 300000) {
                return new byte[0];
            }
            byte[] data = new byte[size];

            inputStream.read(data,0, size);

            return data;
    }
}
