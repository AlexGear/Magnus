package ru.eltex.magnus.server.db.storages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.StorageException;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentsStorageTests {
    @BeforeAll
    @AfterAll
    static void cleanup() {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        try {
            for(Department d : storage.getAllDepartments()) {
                storage.removeDepartmentById(d.getId());
            }
        } catch (StorageException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testInsertGetUpdateRemoveDepartment() {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();

        Department d = new Department(0, "asdf");
        testInsertGetUpdateRemoveDepartment(storage, d);

        d = new Department(100, "Dev");
        testInsertGetUpdateRemoveDepartment(storage, d);

        d = new Department(5, "XYZ");
        testInsertGetUpdateRemoveDepartment(storage, d);
    }

    void testInsertGetUpdateRemoveDepartment(DepartmentsStorage storage, Department d) {
        try {
            assertTrue(storage.insertDepartmentAndAssignId(d));
            Department d2 = storage.getDepartmentById(d.getId());
            assertEquals(d, d2);

            d.setName(d.getName() + " department");
            assertTrue(storage.updateDepartment(d));
            d2 = storage.getDepartmentById(d.getId());
            assertEquals(d, d2);

            assertTrue(storage.removeDepartmentById(d.getId()));

            assertFalse(storage.getAllDepartments().contains(d));
        } catch (StorageException e) {
            fail(e.getMessage());
        }
    }
}
