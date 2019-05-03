package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Department;

import java.util.List;

public interface DepartmentsStorage {
    List<Department> getAllDepartments();
    Department getDepartmentById(int id);
    boolean insertDepartmentAndAssignId(Department department);
    boolean updateDepartment(Department department);
    boolean removeDepartmentById(int id);
}
