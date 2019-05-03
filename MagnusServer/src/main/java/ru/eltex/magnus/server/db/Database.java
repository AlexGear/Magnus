package ru.eltex.magnus.server.db;

import ru.eltex.magnus.server.db.dataclasses.*;
import ru.eltex.magnus.server.db.storages.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Database implements EmployeesStorage, DepartmentsStorage,
        OfflineStreamersStorage, ViewersStorage, AdminStorage {

    private static final String EMPLOYEES_TABLE = "employees";
    private static final String DEPARTMENTS_TABLE = "departments";
    private static final String VIEWERS_TABLE = "viewers";
    private static final String OFFLINE_STREAMERS_TABLE = "offline_streamers";
    private static final String ADMIN_TABLE = "admin";

    private final String connectionURL;
    private final String login;
    private final String password;

    public Database(DatabaseProperties properties) {
        connectionURL = properties.getConnectionURL();
        if (connectionURL == null) throw new NullPointerException();

        login = properties.getLogin();
        if (login == null) throw new NullPointerException();

        password = properties.getPassword();
        if (password == null) throw new NullPointerException();
    }

    @Override
    public Admin getAdmin() {
        final String sql = "SELECT login, password FROM " + ADMIN_TABLE;
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql))
        {
            if (!rs.first()) {
                return null;
            }
            return new Admin(rs.getString("login"),
                    rs.getString("password"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Department getDepartmentById(int id) {
        final String sql = "SELECT name FROM " + DEPARTMENTS_TABLE + " WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if(!rs.first()) {
                    return null;
                }
                return new Department(id, rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Department> getAllDepartments() {
        final String sql = "SELECT id, name FROM " + DEPARTMENTS_TABLE;
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql))
        {
            List<Department> result = new ArrayList<>();
            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                result.add(new Department(id, name));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Employee getEmployeeByLogin(String login) {
        final String sql = "SELECT" +
                " password, " + EMPLOYEES_TABLE + ".name," +
                " department_id, " + DEPARTMENTS_TABLE + ".name, " +
                " job_name," +
                " phone_number, " +
                " email FROM " + EMPLOYEES_TABLE +
                " LEFT JOIN " + DEPARTMENTS_TABLE + " ON " + DEPARTMENTS_TABLE + ".id = department_id" +
                " WHERE login = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if(!rs.first()) {
                    return null;
                }
                return new Employee() {{
                    setLogin(login);
                    setPassword(rs.getString("password"));
                    setName(rs.getString(EMPLOYEES_TABLE + ".name"));
                    setDepartment(new Department(
                        rs.getInt("department_id"),
                        rs.getString(DEPARTMENTS_TABLE + ".name"))
                    );
                    setJobName(rs.getString("job_name"));
                    setPhoneNumber(rs.getString("phone_number"));
                    setEmail(rs.getString("email"));
                }};
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        final String sql = "SELECT" +
                " password, " + EMPLOYEES_TABLE + ".name," +
                " department_id, " + DEPARTMENTS_TABLE + ".name, " +
                " job_name," +
                " phone_number, " +
                " email FROM " + EMPLOYEES_TABLE +
                " LEFT JOIN " + DEPARTMENTS_TABLE + " ON " + DEPARTMENTS_TABLE + ".id = department_id";
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql))
        {
            List<Employee> result = new ArrayList<>();
            while(rs.next()) {
                result.add(new Employee() {{
                    setLogin(login);
                    setPassword(rs.getString("password"));
                    setName(rs.getString(EMPLOYEES_TABLE + ".name"));
                    setDepartment(new Department(
                            rs.getInt("department_id"),
                            rs.getString(DEPARTMENTS_TABLE + ".name"))
                    );
                    setJobName(rs.getString("job_name"));
                    setPhoneNumber(rs.getString("phone_number"));
                    setEmail(rs.getString("email"));
                }});
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public OfflineStreamer getOfflineStreamerByEmployee(Employee employee) {
        return null;
    }

    @Override
    public OfflineStreamer getOfflineStreamerByLogin(String login) {
        return null;
    }

    @Override
    public List<OfflineStreamer> getAllOfflineStreamers() {
        return null;
    }

    @Override
    public Viewer getViewerByLogin(String login) {
        return null;
    }

    @Override
    public List<Viewer> getAllViewers() {
        return null;
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(connectionURL, login, password);
    }
}