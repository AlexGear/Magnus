package ru.eltex.magnus.server.db;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class OfflineStreamerTests {
    @Test
    void testOfflineStreamerConstructorSetsValues() {
        Employee employee = new Employee();
        Timestamp lastSeen = new Timestamp(0);
        OfflineStreamer o = new OfflineStreamer(employee, lastSeen);
        assertEquals(employee, o.getEmployee());
        assertEquals(lastSeen, o.getLastSeen());
    }

    @Test
    void testOfflineStreamerGetsSets() {
        OfflineStreamer o = new OfflineStreamer();

        Employee employee = new Employee();
        o.setEmployee(employee);
        assertEquals(employee, o.getEmployee());

        Timestamp lastSeen = new Timestamp(0);
        o.setLastSeen(lastSeen);
        assertEquals(lastSeen, o.getLastSeen());
    }
}