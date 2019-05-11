package ru.eltex.magnus.server.db.dataclasses;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Viewer;

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

    @Test
    void testViewerEqualsAndHashCode() {
        String login = "johny";
        String password = "soooooooo";
        String name = "Jij";

        Viewer v1 = new Viewer(login, password, name);
        assertEquals(v1, v1);
        Viewer v2 = new Viewer(login, password, name);
        assertEquals(v1, v2);
        assertEquals(v2, v1);
        assertEquals(v1.hashCode(), v2.hashCode());

        v1.setName(v1.getName() + "xxx");
        assertNotEquals(v1, v2);
        assertNotEquals(v2, v1);
        assertNotEquals(v1.hashCode(), v2.hashCode());
    }
}
