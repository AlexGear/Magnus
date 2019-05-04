package ru.eltex.magnus.server.db;

import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

public class DatabaseFileProperties implements DatabaseProperties {
    private static final String CONNECTION_URL_PROPERTY = "mysql.connection_url";
    private static final String LOGIN_PROPERTY = "mysql.login";
    private static final String PASSWORD_PROPERTY = "mysql.password";

    private final String connectionURL;
    private final String login;
    private final String password;

    public DatabaseFileProperties(String filename) throws IOException, NoSuchElementException {
        try (FileReader reader = new FileReader(filename)) {
            Properties properties = new Properties();
            properties.load(reader);

            connectionURL = properties.getProperty(CONNECTION_URL_PROPERTY);
            if(connectionURL == null) {
                throw new NoSuchElementException(CONNECTION_URL_PROPERTY + " was not found");
            }
            login = properties.getProperty(LOGIN_PROPERTY);
            if(login == null) {
                throw new NoSuchElementException(LOGIN_PROPERTY + " was not found");
            }
            password = properties.getProperty(PASSWORD_PROPERTY);
            if(password == null) {
                throw new NoSuchElementException(PASSWORD_PROPERTY + " was not found");
            }
        }
    }

    @Override
    public String getConnectionURL() {
        return connectionURL;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
