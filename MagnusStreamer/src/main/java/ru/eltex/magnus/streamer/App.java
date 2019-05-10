package ru.eltex.magnus.streamer;


public class App {

    private static final String PROPERTIES_FILE_PATH = "magnus.properties";
    public static final PropertiesManager properties = new PropertiesManager();
    public static final Streamer streamer = new Streamer();

    public static void main(String[] args) {
        GUI.init();
        if (!properties.loadFromFile(PROPERTIES_FILE_PATH)) {
            GUI.sendUserErrorMsg("Can't load properties");
            return;
        }

        Thread stream = new Thread(streamer);
        stream.setDaemon(true);
        stream.start();
    }
}