package ru.eltex.magnus.server.db.dataclasses;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Admin;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTests {
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
}
