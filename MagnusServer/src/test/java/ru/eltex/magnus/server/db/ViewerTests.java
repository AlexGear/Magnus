package ru.eltex.magnus.server.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewerTests {
    @Test
    void testViewerConstructorSetsValues() {
        String login = "cartman";
        String password = "sweeeeeet";
        String name = "Eric";

        Viewer v = new Viewer(login, password, name);
        assertEquals(login, v.getLogin());
        assertEquals(password, v.getPassword());
        assertEquals(name, v.getName());
    }

    @Test
    void testViewerSetsGets() {
        Viewer v = new Viewer();

        String login = "sos";
        v.setLogin(login);
        assertEquals(login, v.getLogin());

        String password = "ses";
        v.setPassword(password);
        assertEquals(password, v.getPassword());

        String name = "Sas";
        v.setName(name);
        assertEquals(name, v.getName());
    }
}
