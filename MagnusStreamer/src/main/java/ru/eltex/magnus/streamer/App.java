package ru.eltex.magnus.streamer;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class App {

    private static final String PROPERTIES_FILE_PATH = "magnus.properties";

    private static final Logger LOG = LogManager.getLogger(App.class);

    public static final PropertiesManager PROPERTIES = new PropertiesManager(PROPERTIES_FILE_PATH);
    public static final Streamer STREAMER = new Streamer();

    public static void main(String[] args) {
        if (!PROPERTIES.load()) {
            LOG.info("Failed to load properties file: '" + PROPERTIES_FILE_PATH + "'. Trying to create a new one...");
            PROPERTIES.setServerAddress("");
            PROPERTIES.setServerPort(0);
            PROPERTIES.setLogin("");
            PROPERTIES.setPassword("");
            if(!PROPERTIES.save()) {
                String message = "Could not load properties file '" + PROPERTIES_FILE_PATH +
                        "'.\nTried to recreate the file but also failed";
                LOG.fatal(message);
                String title = "Error loading properties";
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        GUI.init();
        STREAMER.init();
    }
}