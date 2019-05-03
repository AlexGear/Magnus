package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Department;

import java.util.List;

public interface DepartmentsStorage {
    Department getDepartmentById(int id);
    List<Department> getAllDepartments();
}
