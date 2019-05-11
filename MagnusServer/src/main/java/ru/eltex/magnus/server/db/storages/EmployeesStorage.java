package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Employee;

import java.util.List;

/**
 * Represents an abstract {@link Employee}s storage
 */
public interface EmployeesStorage {
    /**
     * Gets all the employees stored in the storage
     * @return {@link List} of {@link Employee}s stored in the storage
     */
    List<Employee> getAllEmployees();

    /**
     * Searches in the storage for an employee with specified login and returns it
     * @param login the login of the {@link Employee}
     * @return the {@link Employee} if it was found, otherwise {@literal null}
     */
    Employee getEmployeeByLogin(String login);

    /**
     * Inserts new employee into the storage
     * @param employee the {@link Employee} object to add insert into the storage
     * @return {@literal true} if added successfully and {@literal false} if an error occurred
     * or an employee with the same login is already contained in the storage
     */
    boolean insertEmployee(Employee employee);

    /**
     * Searches for the employee with the same id and updates its fields
     * @param employee the {@link Employee} object whose fields are used to perform the update
     * @return {@literal true} if updated successfully and {@literal false} if an error occurred
     * or the employee was not found
     */
    boolean updateEmployee(Employee employee);

    /**
     * Searches for the employee by its login and removes it from the storage
     * @param login the login of the {@link Employee} that should be removed from the storage
     * @return {@literal true} if removed successfully and and {@literal false} if an error occurred
     * or the employee was not found
     */
    boolean removeEmployeeByLogin(String login);
}
