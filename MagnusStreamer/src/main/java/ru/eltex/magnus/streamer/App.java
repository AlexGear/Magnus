package ru.eltex.magnus.streamer;

import java.io.IOException;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        GUI graphicalInreteface = new GUI();

        ArrayList<String> data = new StreamerDataStorage().loadFromFile();
        if(!(data == null)){
            String login = data.get(0);
            String password = data.get(1);
            String host = data.get(2);
            String port = data.get(3);
            graphicalInreteface.setStreamerDataArray(data);
            new Thread(()-> new Streamer(login, password, host, Integer.valueOf(port))).start();
        }

    }
}