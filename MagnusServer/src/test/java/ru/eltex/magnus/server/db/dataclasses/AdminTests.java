package ru.eltex.magnus.server.db.dataclasses;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Admin;

import static org.junit.jupiter.api.Assertions.*;

class AdminTests {
    @Test
    void testAdminConstructorSetsValues() {
        String login = "admin";
        String password = "qwerty";
        Admin a = new Admin(login, password);
        assertEquals(login, a.getLogin());
        assertEquals(password, a.getPassword());
    }

    @Test
    void testAdminSetsGets() {
        Admin a = new Admin();

        String login = "moe";
        a.setLogin(login);
        assertEquals(login, a.getLogin());

        String password = "n&*1-kA";
        a.setPassword(password);
        assertEquals(password, a.getPassword());
    }

    @Test
    void testAdminEqualsAndHashCode() {
        String login = "adminium";
        String password = "passwordium";
        Admin a1 = new Admin(login, password);
        assertEquals(a1, a1);
        Admin a2 = new Admin(login, password);

        assertEquals(a1, a2);
        assertEquals(a2, a1);
        assertEquals(a1.hashCode(), a2.hashCode());

        a1.setLogin(a1.getLogin() + "asdf");
        assertNotEquals(a1, a2);
        assertNotEquals(a2, a1);
        assertNotEquals(a1.hashCode(), a2.hashCode());
    }
}
