package ru.eltex.magnus.server.db.dataclasses;

import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTests {
    @Test
    void testEmployeeGetsSets() {
        Employee e = new Employee();
        String login = "some_login";
        e.setLogin(login);
        assertEquals(login, e.getLogin());

        String password = "some_password";
        e.setPassword(password);
        assertEquals(password, e.getPassword());

        String name = "The Big Lebowski";
        e.setName(name);
        assertEquals(name, e.getName());

        Department department = new Department();
        e.setDepartment(department);
        assertEquals(department, e.getDepartment());

        String jobName = "Doctor";
        e.setJobName(jobName);
        assertEquals(jobName, e.getJobName());

        String phoneNumber = "29123091123";
        e.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, e.getPhoneNumber());

        String email = "okey@dokey.com";
        e.setEmail(email);
        assertEquals(email, e.getEmail());
    }

    @Test
    void testEmployeeEqualsAndHashCode() {
        String login = "qwerty", password = "123456", name = "Asdf";
        int depId = 1;
        String depName = "Dev";
        String jobName = "Superintendent", phoneNumber = "0912340912", email = "asdf@asdf.asdf";
        Employee e1 = new Employee();
        e1.setLogin(login);
        e1.setPassword(password);
        e1.setName(name);
        e1.setDepartment(new Department(depId, depName));
        e1.setJobName(jobName);
        e1.setPhoneNumber(phoneNumber);
        e1.setEmail(email);

        assertEquals(e1, e1);
        Employee e2 = new Employee();
        e2.setLogin(login);
        e2.setPassword(password);
        e2.setName(name);
        e2.setDepartment(new Department(depId, depName));
        e2.setJobName(jobName);
        e2.setPhoneNumber(phoneNumber);
        e2.setEmail(email);
        assertEquals(e1, e2);
        assertEquals(e2, e1);
        assertEquals(e1.hashCode(), e2.hashCode());

        e1.setName(e1.getName() + "xyz");
        assertNotEquals(e1, e2);
        assertNotEquals(e2, e1);
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }
}
