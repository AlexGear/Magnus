package ru.eltex.magnus.streamer;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PropertiesManagerTests {
    @Test
    void testPropertiesSaveAndLoad() {
        final String FILENAME = "magnus_test.properties";
        PropertiesManager properties = new PropertiesManager(FILENAME);

        final String SERVER_ADDRESS = "127.0.0.1";
        final int SERVER_PORT = 8080;
        final String LOGIN = "login";
        final String PASSWORD = "password";

        properties.setServerAddress(SERVER_ADDRESS);
        properties.setServerPort(SERVER_PORT);
        properties.setLogin(LOGIN);
        properties.setPassword(PASSWORD);

        assertTrue(properties.save());
        properties = new PropertiesManager(FILENAME);
        assertTrue(properties.load());

        assertEquals(SERVER_ADDRESS, properties.getServerAddress());
        assertEquals(SERVER_PORT, properties.getServerPort());
        assertEquals(LOGIN, properties.getLogin());
        assertEquals(PASSWORD, properties.getPassword());

        new File(FILENAME).deleteOnExit();
    }
}
