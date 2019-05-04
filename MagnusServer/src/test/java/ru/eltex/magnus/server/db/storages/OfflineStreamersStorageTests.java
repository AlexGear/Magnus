package ru.eltex.magnus.server.db.storages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class OfflineStreamersStorageTests {
    private static Department department;
    private static Employee employee;

    @BeforeAll
    static void insertDepartmentAndEmployee() {
        cleanup();

        department = new Department(0, "Some department");
        StoragesProvider.getDepartmentsStorage().insertDepartmentAndAssignId(department);

        employee = new Employee();
        employee.setLogin("iamoffline");
        employee.setPassword("123");
        employee.setName("Lisa");
        employee.setDepartment(department);
        employee.setJobName("Teacher");
        employee.setPhoneNumber("0912309");
        employee.setEmail("lkajsdf@lkjadsf.ru");
        StoragesProvider.getEmployeesStorage().insertEmployee(employee);
    }

    @AfterAll
    static void removeDepartmentAndEmployee() {
        StoragesProvider.getEmployeesStorage().removeEmployeeByLogin(employee.getLogin());
        StoragesProvider.getDepartmentsStorage().removeDepartmentById(department.getId());

        cleanup();
    }

    static void cleanup() {
        OfflineStreamersStorage storage = StoragesProvider.getOfflineStreamersStorage();
        for(OfflineStreamer o : storage.getAllOfflineStreamers()) {
            storage.removeOfflineStreamerByLogin(o.getLogin());
        }
    }

    @Test
    void testInsertGetUpdateRemoveOfflineStreamer() {
        OfflineStreamersStorage storage = StoragesProvider.getOfflineStreamersStorage();

        OfflineStreamer o = new OfflineStreamer(employee.getLogin(), new Timestamp(10000000));
        testInsertGetUpdateRemoveOfflineStreamer(storage, o);

        o = new OfflineStreamer(employee.getLogin(), new Timestamp(100));
        testInsertGetUpdateRemoveOfflineStreamer(storage, o);

        o = new OfflineStreamer(employee.getLogin(), new Timestamp(99999));
        testInsertGetUpdateRemoveOfflineStreamer(storage, o);
    }

    void testInsertGetUpdateRemoveOfflineStreamer(OfflineStreamersStorage storage, OfflineStreamer o) {
        assertTrue(storage.insertOfflineStreamer(o));
        OfflineStreamer o2 = storage.getOfflineStreamerByLogin(o.getLogin());
        assertEquals(o, o2);

        o.setLastSeen(new Timestamp(19812453));
        assertTrue(storage.updateOfflineStreamer(o));
        o2 = storage.getOfflineStreamerByLogin(o.getLogin());
        assertEquals(o, o2);

        assertTrue(storage.removeOfflineStreamerByLogin(o.getLogin()));

        assertFalse(storage.getAllOfflineStreamers().contains(o));
    }

    @Test
    void testImpossibilityToInsertOfflineStreamerWithNonexistentLogin() {
        OfflineStreamersStorage storage = StoragesProvider.getOfflineStreamersStorage();
        OfflineStreamer o = new OfflineStreamer(employee.getLogin() + "imnotreal", new Timestamp(0));
        assertFalse(storage.insertOfflineStreamer(o));
    }
}
