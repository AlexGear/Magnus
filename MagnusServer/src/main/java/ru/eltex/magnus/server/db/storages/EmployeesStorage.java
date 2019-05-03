package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Employee;

import java.util.List;

public interface EmployeesStorage {
    Employee getEmployeeByLogin(String login);
    List<Employee> getAllEmployees();
}
