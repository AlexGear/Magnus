package ru.eltex.magnus.server.db.storages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeesStorageTests {
    private static Department department = new Department(0, "Some department");

    @BeforeAll
    static void insertDepartment() {
        cleanup();

        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        storage.insertDepartmentAndAssignId(department);
    }

    @AfterAll
    static void removeDepartment() {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        storage.removeDepartmentById(department.getId());

        cleanup();
    }

    static void cleanup() {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        for(Employee d : storage.getAllEmployees()) {
            storage.removeEmployeeByLogin(d.getLogin());
        }
    }

    @Test
    void testInsertGetUpdateRemoveEmployee() {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();

        Employee e = new Employee();
        e.setLogin("pip");
        e.setPassword("123");
        e.setName("Pepper");
        e.setDepartment(department);
        e.setJobName("Cooker");
        e.setPhoneNumber("09120123");
        e.setEmail("happy@dude.com");
        testInsertGetUpdateRemoveEmployee(storage, e);

        e = new Employee();
        e.setLogin("pip");
        e.setPassword("123");
        e.setName("Pepper");
        e.setDepartment(department);
        e.setJobName("Cooker");
        e.setPhoneNumber("09120123");
        e.setEmail("happy@dude.com");
        testInsertGetUpdateRemoveEmployee(storage, e);

        e = new Employee();
        e.setLogin("pip");
        e.setPassword("123");
        e.setName("Pepper");
        e.setDepartment(department);
        e.setJobName("Cooker");
        e.setPhoneNumber("09120123");
        e.setEmail("happy@dude.com");
        testInsertGetUpdateRemoveEmployee(storage, e);
    }

    void testInsertGetUpdateRemoveEmployee(EmployeesStorage storage, Employee e) {
        assertTrue(storage.insertEmployee(e));
        Employee e2 = storage.getEmployeeByLogin(e.getLogin());
        assertEquals(e, e2);

        e.setJobName(e.getJobName() + "test");
        e.setName("Dr. " + e.getName());
        assertTrue(storage.updateEmployee(e));
        e2 = storage.getEmployeeByLogin(e.getLogin());
        assertEquals(e, e2);

        assertTrue(storage.removeEmployeeByLogin(e.getLogin()));

        assertFalse(storage.getAllEmployees().contains(e));
    }

    @Test
    void testImpossibilityToInsertEmployeeWithNonexistentDepartment() {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        Department d2 = new Department(department.getId() + 5, "Moneymaking");
        Employee e = new Employee();
        e.setLogin("pip");
        e.setPassword("123");
        e.setName("Pepper");
        e.setDepartment(d2);
        e.setJobName("Cooker");
        e.setPhoneNumber("09120123");
        e.setEmail("happy@dude.com");

        assertFalse(storage.insertEmployee(e));
    }
}
