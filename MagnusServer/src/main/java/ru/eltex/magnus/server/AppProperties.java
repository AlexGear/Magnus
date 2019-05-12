package ru.eltex.magnus.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.eltex.magnus.server.db.DatabaseProperties;
import ru.eltex.magnus.server.streamers.StreamersServerProperties;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Provides application properties loaded from a file<br>
 *     The properties' names searched in the files are
 *     <ol>
 *         <li>MySQL connection URL: {@value CONNECTION_URL_PROPERTY}</li>
 *         <li>MySQL login: {@value LOGIN_PROPERTY}</li>
 *         <li>MySQL password: {@value PASSWORD_PROPERTY}</li>
 *         <li>Streamers server port: {@value SERVER_PORT_PROPERTY}</li>
 *     </ol>
 */
public class AppProperties implements DatabaseProperties, StreamersServerProperties {
    private static final String CONNECTION_URL_PROPERTY = "mysql.connection_url";
    private static final String LOGIN_PROPERTY = "mysql.login";
    private static final String PASSWORD_PROPERTY = "mysql.password";
    private static final String SERVER_PORT_PROPERTY = "server.port";

    private static final Logger LOG = LogManager.getLogger(AppProperties.class);

    private final String connectionURL;
    private final String login;
    private final String password;
    private final int serverPort;

    /**
     * Allocates a new {@link AppProperties} object and loads properties from the specified file
     * @param filename the file to load properties from
     * @throws IOException thrown if and error occurred while reading file
     * @throws NoSuchElementException thrown if any of properties was not found
     * @throws NumberFormatException thrown if provided server port is not a number
     * @throws IllegalArgumentException thrown if provided server port is out of range [0; 65535]
     * found in the properties file
     */
    public AppProperties(String filename) throws IOException, NoSuchElementException,
            NumberFormatException, IllegalArgumentException {

        LOG.info("Loading AppProperties from '" + filename + "'");
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

            String portStr = properties.getProperty(SERVER_PORT_PROPERTY);
            if(portStr == null) {
                throw new NoSuchElementException(SERVER_PORT_PROPERTY + " was not found");
            }
            serverPort = Integer.parseInt(portStr);
            if(serverPort < 0 || serverPort > 65535) {
                throw new IllegalArgumentException("Server port is out of range [0; 65535]: " + serverPort);
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

    @Override
    public int getServerPort() {
        return serverPort;
    }
}
