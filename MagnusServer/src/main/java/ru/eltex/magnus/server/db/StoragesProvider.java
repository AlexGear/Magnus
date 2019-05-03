package ru.eltex.magnus.server.db;

import ru.eltex.magnus.server.db.storages.*;

import java.io.IOException;

public class StoragesProvider {
    private static final String PROPERTIES_FILENAME = "magnus.properties";
    private static final Database DATABASE_INSTANCE;

    static {
        try {
            DatabaseProperties properties = new DatabaseFileProperties(PROPERTIES_FILENAME);
            DATABASE_INSTANCE = new Database(properties);
        } catch (IOException e) {
            String msg = "Failed to load DB properties from '" + PROPERTIES_FILENAME + "'";
            throw new Error(msg, e);
        }
    }

    public static EmployeesStorage getEmployeesStorage() {
        return DATABASE_INSTANCE;
    }

    public static DepartmentsStorage getDepartmentsStorage() {
        return DATABASE_INSTANCE;
    }

    public static ViewersStorage getViewersStorage() {
        return DATABASE_INSTANCE;
    }

    public static OfflineStreamersStorage getOfflineStreamersStorage() {
        return DATABASE_INSTANCE;
    }

    public static AdminStorage getAdminStorage() {
        return DATABASE_INSTANCE;
    }
}
