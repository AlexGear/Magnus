package ru.eltex.magnus.server;

import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.String;
import java.util.ArrayList;

public class StreamersServer {

    static ArrayList<StreamerRequester> streamers;

    public static void start() {
        new Thread(() -> {
            try(ServerSocket server = new ServerSocket(8081)) {
                System.out.println("Server Started");
                streamers = new ArrayList<>();
                while (!server.isClosed()){
                    System.out.println("Whaiting for new client");
                    Socket uncheckedStreamer = server.accept();
                    uncheckedStreamer.setTcpNoDelay(true);
                    System.out.println("New Client Connected");
                    new Thread(() -> waitingForStreamerSignUp(uncheckedStreamer)).run();
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }).run();
    }

    static void waitingForStreamerSignUp(Socket streamer){
        try  {
            DataInputStream inputStream = new DataInputStream(streamer.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(streamer.getOutputStream());

            int size = inputStream.readInt();//inputStream.read();
            byte[] newbyte = new byte[size];
            inputStream.read(newbyte);
            String authString = new String(newbyte);

            String[] authArray = authString.split(":");
            boolean verificated = checkAuthData(authArray);
            System.out.println("Recieved auth data: " + authArray[0] + " " + authArray[1]);

            if(verificated){
                streamers.add(new StreamerRequester(streamer,inputStream, outputStream, authArray[0]));
                String answer = "verified";
                outputStream.writeInt(answer.length());
                outputStream.flush();
                outputStream.write(answer.getBytes());
                outputStream.flush();
            }
            else {
                String answer = "failed";
                outputStream.writeInt(answer.length());
                outputStream.flush();
                outputStream.write(answer.getBytes());
                outputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkAuthData(String[] authArray) {
        if (authArray.length != 2) return false;
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        Employee employee = storage.getEmployeeByLogin(authArray[0]);
        if (employee == null) return false;
        return employee.getPassword().equals(authArray[1]);
    }

    public static StreamerRequester getStreamerReqByLogin(String login){
        return streamers.stream().filter(x->x.getLogin().equals(login)).findFirst().get();
    }

}