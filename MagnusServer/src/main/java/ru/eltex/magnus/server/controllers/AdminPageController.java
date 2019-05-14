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
import ru.eltex.magnus.server.db.dataclasses.Viewer;
import ru.eltex.magnus.server.db.storages.DepartmentsStorage;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;
import ru.eltex.magnus.server.db.storages.ViewersStorage;

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

    @RequestMapping("/admin/add_department")
    public ResponseEntity<String> addDepartment(@RequestParam("name") String name) {
        if (name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
        }
        Department d = new Department(0, name);
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        if (storage.insertDepartmentAndAssignId(d)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/edit_department")
    public ResponseEntity<String> editDepartment(@RequestParam("id") int id, @RequestParam("name") String name) {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        Department d = storage.getDepartmentById(id);
        if (d == null) {
            String body = "Department with id '" + id + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        if (name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
        }

        d.setName(name);
        if (storage.updateDepartment(d)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/remove_department")
    public ResponseEntity<String> removeDepartment(@RequestParam("id") int id) {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        if (storage.removeDepartmentById(id)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }




    @GetMapping("/admin/get_viewers")
    public List<Viewer> getViewers() {
        List<Viewer> viewers = StoragesProvider.getViewersStorage().getAllViewers();
        viewers.forEach(v -> v.setPassword(""));
        return viewers;
    }

    @RequestMapping("/admin/add_viewer")
    public ResponseEntity<String> addViewer(
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("name") String name
    ) {
        if (login.isEmpty())        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("login is empty");
        if (password.isEmpty())     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password is empty");
        if (name.isEmpty())         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");

        Viewer v = new Viewer();
        v.setLogin(login);
        v.setPassword(new BCryptPasswordEncoder().encode(password));
        v.setName(name);

        if(StoragesProvider.getViewersStorage().insertViewer(v)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/edit_viewer")
    public ResponseEntity<String> editViewer(@RequestParam("login") String login, @RequestParam("name") String name) {
        ViewersStorage storage = StoragesProvider.getViewersStorage();
        Viewer v = storage.getViewerByLogin(login);
        if (v == null) {
            String body = "Viewer with login '" + login + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        if (name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
        }

        v.setName(name);
        if (storage.updateViewer(v)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/change_viewer_password")
    public ResponseEntity<String> changeViewerPassword(
            @RequestParam("login") String login,
            @RequestParam("password") String password
    ) {
        ViewersStorage storage = StoragesProvider.getViewersStorage();
        Viewer v = storage.getViewerByLogin(login);
        if (v == null) {
            String body = "Viewer with login '" + login + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        v.setPassword(new BCryptPasswordEncoder().encode(password));
        if (storage.updateViewer(v)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/remove_viewer")
    public ResponseEntity<String> removeViewer(@RequestParam("login") String login) {
        ViewersStorage storage = StoragesProvider.getViewersStorage();
        if (storage.removeViewerByLogin(login)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(520).body("Unknown error");
    }
}
