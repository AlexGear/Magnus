package ru.eltex.magnus.streamer;


import javax.swing.*;

public class App {

    private static final String PROPERTIES_FILE_PATH = "magnus.properties";
    public static final PropertiesManager PROPERTIES = new PropertiesManager(PROPERTIES_FILE_PATH);
    public static final Streamer STREAMER = new Streamer();

    public static void main(String[] args) {
        if (!PROPERTIES.load()) {
            System.out.println("Creating new properties file...");
            PROPERTIES.setServerAddress("");
            PROPERTIES.setServerPort(0);
            PROPERTIES.setLogin("");
            PROPERTIES.setPassword("");
            if(!PROPERTIES.save()) {
                String message = "Could not load properties file '" + PROPERTIES_FILE_PATH +
                        "'.\nTried to recreate the file but also failed";
                String title = "Error loading properties";
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        GUI.init();
        STREAMER.init();
    }
}