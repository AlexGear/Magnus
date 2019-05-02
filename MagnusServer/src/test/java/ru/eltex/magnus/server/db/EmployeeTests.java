package ru.eltex.magnus.server.db;

import org.junit.jupiter.api.Test;

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
}
