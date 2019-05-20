package ru.eltex.magnus.server.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.magnus.server.StorageException;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.Viewer;
import ru.eltex.magnus.server.db.storages.DepartmentsStorage;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;
import ru.eltex.magnus.server.db.storages.ViewersStorage;
import ru.eltex.magnus.server.streamers.StreamersServer;

import java.util.List;

@RestController
public class AdminPageController {

    private static final Logger LOG = LogManager.getLogger(AdminPageController.class);

    @GetMapping("/admin/get_employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = null;
        try {
            employees = StoragesProvider.getEmployeesStorage().getAllEmployees();
            employees.forEach(e -> e.setPassword(""));
        } catch (StorageException e) {
            LOG.warn(e.getMessage());
        }
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

        String errorMsg = "";
        if (login.isEmpty())        errorMsg = "login is empty";
        if (password.isEmpty())     errorMsg = "password is empty";
        if (name.isEmpty())         errorMsg = "name is empty";
        if (jobName.isEmpty())      errorMsg = "jobName is empty";
        if (phoneNumber.isEmpty())  errorMsg = "phoneNumber is empty";
        if (email.isEmpty())        errorMsg = "email is empty";

        if(!errorMsg.isEmpty()) {
            LOG.warn("Failed to add new employee: " + errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }

        Employee e = new Employee();
        e.setLogin(login);
        e.setPassword(new BCryptPasswordEncoder().encode(password));
        e.setName(name);
        Department d = StoragesProvider.getDepartmentsStorage().getDepartmentById(departmentId);
        if (d == null) {
            String body = "Department with id '" + departmentId + "' not found";
            LOG.warn("Failed to add new employee: " + body);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        e.setDepartment(d);
        e.setJobName(jobName);
        e.setPhoneNumber(phoneNumber);
        e.setEmail(email);

        try {
            if(StoragesProvider.getEmployeesStorage().insertEmployee(e)) {
                LOG.info("Add new employee " + e.toString());
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException ex) {
            LOG.warn("Failed to add new employee: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        LOG.warn("Failed to add new employee : Unknown error");
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
        Employee e = null;
        try {
            e = storage.getEmployeeByLogin(login);
            if (e == null) {
                String body = "User with login '" + login + "' not found";
                LOG.warn("Failed to edit employee: " + body);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }

            if (name != null) e.setName(name);
            if (departmentId != null) {
                Department d = StoragesProvider.getDepartmentsStorage().getDepartmentById(departmentId);
                if (d == null) {
                    String body = "Department with id '" + departmentId + "' not found";
                    LOG.warn("Failed to edit employee: " + body);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
                }
                e.setDepartment(d);
            }
            if (jobName != null) e.setJobName(jobName);
            if (phoneNumber != null) e.setPhoneNumber(phoneNumber);
            if (email != null) e.setEmail(email);

            if (storage.updateEmployee(e)) {
                LOG.info("Employee " + e.toString() + " edited");
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException ex) {
            LOG.warn("Failed to edit employee " + e.toString() + " : " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        LOG.warn("Failed to edit employee : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/change_employee_password")
    public ResponseEntity<String> changeEmployeePassword(
            @RequestParam("login") String login,
            @RequestParam("password") String password
    ) {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        Employee e = null;
        try {
            e = storage.getEmployeeByLogin(login);
            if (e == null) {
                String body = "User with login '" + login + "' not found";
                LOG.warn("Failed to edit employee password : " + body);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            e.setPassword(new BCryptPasswordEncoder().encode(password));
            if (storage.updateEmployee(e)) {
                LOG.info("Changed password to employee " + e.toString());
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException ex) {
            LOG.warn("Failed to edit password : " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        LOG.warn("Failed to edit employee password : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/remove_employee")
    public ResponseEntity<String> removeEmployee(@RequestParam("login") String login) {
        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
        try {
            if (storage.removeEmployeeByLogin(login)) {
                LOG.info("Employee with login " + login + " removed");
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to remove employee : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to remove employee : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @GetMapping("/admin/get_departments")
    public List<Department> getDepartments() {
        try {
            return StoragesProvider.getDepartmentsStorage().getAllDepartments();
        } catch (StorageException e) {
            LOG.warn(e.getMessage());
            return null;
        }
    }

    @RequestMapping("/admin/add_department")
    public ResponseEntity<String> addDepartment(@RequestParam("name") String name) {
        if (name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
        }
        Department d = new Department(0, name);
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        try {
            if (storage.insertDepartmentAndAssignId(d)) {
                LOG.info("Add department " + name);
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to add department " + name + " : "  + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to add department : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/edit_department")
    public ResponseEntity<String> editDepartment(@RequestParam("id") int id, @RequestParam("name") String name) {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        Department d = storage.getDepartmentById(id);
        if (d == null) {
            String body = "Department with id '" + id + "' not found";
            LOG.warn("Failed to edit department: " + body);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        if (name.isEmpty()) {
            LOG.warn("Failed to edit department: name is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
        }

        d.setName(name);
        try {
            if (storage.updateDepartment(d)) {
                LOG.info("Department " + name + " edited");
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Filed to edit department: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to edit department : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/remove_department")
    public ResponseEntity<String> removeDepartment(@RequestParam("id") int id) {
        DepartmentsStorage storage = StoragesProvider.getDepartmentsStorage();
        try {
            if (storage.removeDepartmentById(id)) {
                LOG.info("Department with id " + id + " removed");
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to remove department: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to remove department : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }




    @GetMapping("/admin/get_viewers")
    public List<Viewer> getViewers() {
        List<Viewer> viewers = null;
        try {
            viewers = StoragesProvider.getViewersStorage().getAllViewers();
            viewers.forEach(v -> v.setPassword(""));
        } catch (StorageException e) {
            LOG.warn(e.getMessage());
        }
        return viewers;
    }

    @RequestMapping("/admin/add_viewer")
    public ResponseEntity<String> addViewer(
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("name") String name
    ) {
        String errorMsg = "";
        if (login.isEmpty())        errorMsg = "login is empty";
        if (password.isEmpty())     errorMsg = "password is empty";
        if (name.isEmpty())         errorMsg = "name is empty";

        if(!errorMsg.isEmpty()){
            LOG.warn("Failed to add viewer: "+ errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }

        Viewer v = new Viewer();
        v.setLogin(login);
        v.setPassword(new BCryptPasswordEncoder().encode(password));
        v.setName(name);

        try {
            if(StoragesProvider.getViewersStorage().insertViewer(v)) {
                LOG.info("Add viewer " + v.toString());
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to add viewer " + v.toString() +  ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to add new viewer : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/edit_viewer")
    public ResponseEntity<String> editViewer(@RequestParam("login") String login, @RequestParam("name") String name) {
        ViewersStorage storage = StoragesProvider.getViewersStorage();
        Viewer v = null;
        try {
            v = storage.getViewerByLogin(login);
            if (v == null) {
                String body = "Viewer with login '" + login + "' not found";
                LOG.warn("Failed to edit viewer: " + body);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            if (name.isEmpty()) {
                LOG.warn("Failed to edit viewer: name is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is empty");
            }

            v.setName(name);
            if (storage.updateViewer(v)) {
                LOG.info("Add viewer: " + v.toString());
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to edit viewer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to edit viewer : Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/change_viewer_password")
    public ResponseEntity<String> changeViewerPassword(
            @RequestParam("login") String login,
            @RequestParam("password") String password
    ) {
        ViewersStorage storage = StoragesProvider.getViewersStorage();
        Viewer v = null;
        try {
            v = storage.getViewerByLogin(login);
            if (v == null) {
                String body = "Viewer with login '" + login + "' not found";
                LOG.warn("Failed to change viewer password: " + body);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            v.setPassword(new BCryptPasswordEncoder().encode(password));
            if (storage.updateViewer(v)) {
                LOG.info("Viewer password changed: " + v.toString());
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to change viewer password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to change viewer password: Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }

    @RequestMapping("/admin/remove_viewer")
    public ResponseEntity<String> removeViewer(@RequestParam("login") String login) {
        ViewersStorage storage = StoragesProvider.getViewersStorage();
        try {
            if (storage.removeViewerByLogin(login)) {
                LOG.info("Viewer with login " + login + " removed");
                return ResponseEntity.ok("OK");
            }
        } catch (StorageException e) {
            LOG.warn("Failed to remove viewer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        LOG.warn("Failed to remove viewer: Unknown error");
        return ResponseEntity.status(520).body("Unknown error");
    }
}
