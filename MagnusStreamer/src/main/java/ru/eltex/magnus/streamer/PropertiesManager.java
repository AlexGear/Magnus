package ru.eltex.magnus.streamer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;


public class PropertiesManager {

    private String serverAddress;
    private int serverPort;
    private String login;
    private String password;

    public boolean loadFromFile(String fileName) {
        Properties prop = new Properties();
        try (FileReader reader = new FileReader(fileName, Charset.forName("UTF-8"))) {
            prop.load(reader);
        } catch (IOException e) {
            System.out.println("Failed to read config file: " + fileName);
            return false;
        }
        setServerAddress(prop.getProperty("serverAddress", ""));
        setServerPort(Integer.parseInt(prop.getProperty("serverPort", "0")));
        setLogin(prop.getProperty("login", ""));
        setPassword(prop.getProperty("password", ""));
        return true;
    }

    public boolean saveToFile(String fileName) {
        Properties prop = new Properties();
        prop.setProperty("serverAddress", getServerAddress());
        prop.put("serverPort", getServerPort());
        prop.put("login", getLogin());
        prop.put("password", getPassword());

        try (FileWriter writer = new FileWriter(fileName, Charset.forName("UTF-8"))) {
            prop.store(writer, "");
        } catch (IOException e) {
            System.out.println("Failed to write config file: " + fileName);
            return false;
        }
        return true;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
