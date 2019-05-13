package ru.eltex.magnus.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;

import java.util.List;

@RestController
public class AdminPageController {
    @GetMapping("/admin/get_employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = StoragesProvider.getEmployeesStorage().getAllEmployees();
        employees.forEach(e -> e.setPassword(""));
        return employees;
    }

    @PostMapping("/admin/edit_employee")
    public ResponseEntity<String> editEmployee(
            @RequestParam("login") String login,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @RequestParam(value = "jobName", required = false) String jobName,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "email", required = false) String email
    ) {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        Employee e = storage.getEmployeeByLogin(login);
        if (e == null) {
            String body = "User with login '" + login + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        if (name != null) e.setName(name);
        if (departmentId != null) {
            Department d = StoragesProvider.getDepartmentsStorage().getDepartmentById(departmentId);
            if (d == null) {
                String body = "Department with id '" + departmentId + "' not found";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            e.setDepartment(d);
        }
        if (jobName != null) e.setJobName(jobName);
        if (phoneNumber != null) e.setPhoneNumber(phoneNumber);
        if (email != null) e.setEmail(email);

        boolean ok = storage.updateEmployee(e);
        return ResponseEntity.status(ok ? 200 : 520).body("Unknown error");
    }

    @GetMapping("/admin/get_departments")
    public List<Department> getDepartments() {
        return StoragesProvider.getDepartmentsStorage().getAllDepartments();
    }
}
