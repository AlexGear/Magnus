package ru.eltex.magnus.server.db.dataclasses;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Department;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTests {
    @Test
    void testDepartmentConstructorSetsValues() {
        int id = 43;
        String name = "Marketing";
        Department d = new Department(id, name);
        assertEquals(id, d.getId());
        assertEquals(name, d.getName());
    }

    @Test
    void testDepartmentGetsSets() {
        Department d = new Department();

        int id = 112;
        d.setId(id);
        assertEquals(id, d.getId());

        String name = "Dev";
        d.setName(name);
        assertEquals(name, d.getName());
    }

    @Test
    void testDepartmentEqualsAndHashCode() {
        int id = 10;
        String name = "Management";
        Department d1 = new Department(id, name);
        assertEquals(d1, d1);
        Department d2 = new Department(id, name);
        assertEquals(d1, d2);
        assertEquals(d2, d1);
        assertEquals(d1.hashCode(), d2.hashCode());

        d1.setId(d1.getId() + 1);
        assertNotEquals(d1, d2);
        assertNotEquals(d2, d1);
        assertNotEquals(d1.hashCode(), d2.hashCode());
    }
}
