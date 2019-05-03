package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Employee;

import java.util.List;

public interface EmployeesStorage {
    List<Employee> getAllEmployees();
    Employee getEmployeeByLogin(String login);
    boolean insertEmployee(Employee employee);
    boolean updateEmployee(Employee employee);
    boolean removeEmployeeByLogin(String login);
}
