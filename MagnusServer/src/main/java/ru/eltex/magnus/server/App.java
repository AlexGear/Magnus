package ru.eltex.magnus.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Admin;
import ru.eltex.magnus.server.db.dataclasses.Department;
import ru.eltex.magnus.server.db.dataclasses.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class App {
    public static void main(String[] args) throws IOException {
        for(Employee employee : StoragesProvider.getEmployeesStorage().getAllEmployees()) {
            System.out.println(employee.getLogin());
            System.out.println(employee.getPassword());
            System.out.println(employee.getName());
            System.out.println(employee.getDepartment().getId());
            System.out.println(employee.getDepartment().getName());
            System.out.println(employee.getJobName());
            System.out.println(employee.getPhoneNumber());
            System.out.println(employee.getEmail());
        }
        for(Department department : StoragesProvider.getDepartmentsStorage().getAllDepartments()) {
            System.out.println(department.getId());
            System.out.println(department.getName());
        }
        Department department = StoragesProvider.getDepartmentsStorage().getDepartmentById(1);
        System.out.println(department.getId());
        System.out.println(department.getName());

        ApplicationContext context = SpringApplication.run(App.class, args);
        System.setProperty("java.awt.headless", "false");
        StreamersServer.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (!"exit".equals(in.readLine().trim().toLowerCase())) ;
        }
        SpringApplication.exit(context);
    }
}