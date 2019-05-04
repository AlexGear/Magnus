package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Department;

import java.util.List;

/**
 * Represents and abstract storage of {@link Department}s
 */
public interface DepartmentsStorage {
    /**
     * @return all the {@link Department}s stored in the storage
     */
    List<Department> getAllDepartments();

    /**
     * Searches for a department with specified id in the storage and returns it
     * @param id the of the {@link Department} to get information of
     * @return the {@link Department} object if record with specified id was found, otherwise {@literal null}
     */
    Department getDepartmentById(int id);

    /**
     * Inserts new department into the storage
     * @param department the {@link Department} to insert into the storage
     * @return {@literal true} if added successfully and {@literal false} if an error occurred or
     * a {@link Department} with the same id is already contained in the storage
     */
    boolean insertDepartmentAndAssignId(Department department);

    /**
     * Searches for the department with the same id in the storage and updates its fields
     * @param department the {@link Department} object whose values will be used to perform update
     * @return {@literal true} if updated successfully and {@literal false} if an error occurred or
     * if department was not found
     */
    boolean updateDepartment(Department department);

    /**
     * Removes the department with specified id from the storage
     * @param id the id of the department to remove from the storage
     * @return {@literal true} if removed successfully and {@literal false} if an error occurred or
     *      * if department was not found
     */
    boolean removeDepartmentById(int id);
}
