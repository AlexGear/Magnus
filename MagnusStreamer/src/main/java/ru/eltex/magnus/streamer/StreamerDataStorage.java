package ru.eltex.magnus.streamer;

import java.io.*;
import java.util.ArrayList;

public class StreamerDataStorage {

    private static String fileName = "streamerData.txt";
    private static ArrayList<String> streamerData;

    public static final int LOGIN = 0;
    public static final int PASSWORD = 1;
    public static final int HOST = 2;
    public static final int PORT = 3;

    public StreamerDataStorage(){
        streamerData = new ArrayList<>();
    }

    public ArrayList<String> loadFromFile(){
        return loadFromFile(fileName);
    }

    public ArrayList<String> loadFromFile(String fileName){
        this.fileName = fileName;
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while (true) {
                String data = bufferedReader.readLine();
                if(data == null) break;
                streamerData.add(data);
            }
            return streamerData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeToFile(ArrayList<String> data){
        return writeToFile(fileName, data);
    }

    public static boolean writeToFile(String fileName, ArrayList<String> data){
        try {
            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(int i = 0; i < data.size(); i++){
                bufferedWriter.write(data.get(i));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
