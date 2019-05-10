package ru.eltex.magnus.streamer;


public class App {

    private static final String PROPERTIES_FILE_PATH = "magnus.properties";
    public static final PropertiesManager PROPERTIES = new PropertiesManager();
    public static final Streamer STREAMER = new Streamer();

    public static void main(String[] args) {
        GUI.init();
        if (!PROPERTIES.loadFromFile(PROPERTIES_FILE_PATH)) {
            GUI.sendUserErrorMsg("Can't load properties");
            System.exit(1);
        }
        STREAMER.init();
    }
}