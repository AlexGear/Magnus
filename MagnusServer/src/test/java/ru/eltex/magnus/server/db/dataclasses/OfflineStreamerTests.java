package ru.eltex.magnus.server.db.dataclasses;

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

    @Test
    void testOfflineStreamerEqualsAndHashCode() {
        String login = "khovansky";
        Timestamp lastSeen = new Timestamp(0);
        OfflineStreamer o1 = new OfflineStreamer(login, lastSeen);
        assertEquals(o1, o1);
        OfflineStreamer o2 = new OfflineStreamer(login, lastSeen);
        assertEquals(o1, o2);
        assertEquals(o2, o1);
        assertEquals(o1.hashCode(), o2.hashCode());

        o1.setLastSeen(new Timestamp(10));
        assertNotEquals(o1, o2);
        assertNotEquals(o2, o1);
        assertNotEquals(o2.hashCode(), o1.hashCode());
    }
}