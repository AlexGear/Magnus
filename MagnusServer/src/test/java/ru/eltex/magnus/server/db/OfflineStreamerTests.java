package ru.eltex.magnus.server.db;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class OfflineStreamerTests {
    @Test
    void testOfflineStreamerConstructorSetsValues() {
        String login = "shaggy";
        Timestamp lastSeen = new Timestamp(0);
        OfflineStreamer o = new OfflineStreamer(login, lastSeen);
        assertEquals(login, o.getLogin());
        assertEquals(lastSeen, o.getLastSeen());
    }

    @Test
    void testOfflineStreamerGetsSets() {
        OfflineStreamer o = new OfflineStreamer();

        String login = "chef";
        o.setLogin(login);
        assertEquals(login, o.getLogin());

        Timestamp lastSeen = new Timestamp(0);
        o.setLastSeen(lastSeen);
        assertEquals(lastSeen, o.getLastSeen());
    }
}