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
}
