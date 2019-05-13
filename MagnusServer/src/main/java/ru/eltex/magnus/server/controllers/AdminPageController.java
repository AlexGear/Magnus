package ru.eltex.magnus.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/admin/add_employee")
    public ResponseEntity<String> addEmployee(
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("departmentId") int departmentId,
            @RequestParam("jobName") String jobName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email
    ) {
        if (login.isEmpty())        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("login is empty");
        if (password.isEmpty())     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password is empty");
        if (name.isEmpty())         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
        if (jobName.isEmpty())      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("jobName is empty");
        if (phoneNumber.isEmpty())  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("phoneNumber is empty");
        if (email.isEmpty())        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email is empty");

        Employee e = new Employee();
        e.setLogin(login);
        e.setPassword(new BCryptPasswordEncoder().encode(password));
        e.setName(name);
        Department d = StoragesProvider.getDepartmentsStorage().getDepartmentById(departmentId);
        if (d == null) {
            String body = "Department with id '" + departmentId + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        e.setDepartment(d);
        e.setJobName(jobName);
        e.setPhoneNumber(phoneNumber);
        e.setEmail(email);

        if(StoragesProvider.getEmployeesStorage().insertEmployee(e)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/edit_employee")
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

        if (storage.updateEmployee(e)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/change_employee_password")
    public ResponseEntity<String> changeEmployeePassword(
            @RequestParam("login") String login,
            @RequestParam("password") String password
    ) {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        Employee e = storage.getEmployeeByLogin(login);
        if (e == null) {
            String body = "User with login '" + login + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        e.setPassword(new BCryptPasswordEncoder().encode(password));
        if (storage.updateEmployee(e)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/remove_employee")
    public ResponseEntity<String> removeEmployee(@RequestParam("login") String login) {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        if (storage.removeEmployeeByLogin(login)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @GetMapping("/admin/get_departments")
    public List<Department> getDepartments() {
        return StoragesProvider.getDepartmentsStorage().getAllDepartments();
    }
}
