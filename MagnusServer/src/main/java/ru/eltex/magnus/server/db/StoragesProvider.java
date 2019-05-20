package ru.eltex.magnus.server.db;

import ru.eltex.magnus.server.App;
import ru.eltex.magnus.server.db.storages.*;

/**
 * Class providing access to different storages:
 * <ul>
 *     <li>{@link EmployeesStorage}</li>
 *     <li>{@link DepartmentsStorage}</li>
 *     <li>{@link ViewersStorage}</li>
 *     <li>{@link OfflineStreamersStorage}</li>
 *     <li>{@link AdminStorage}</li>
 * </ul>
 */
public class StoragesProvider {
    private static final Database DATABASE_INSTANCE = new Database(getDatabaseProperties());

    private static DatabaseProperties getDatabaseProperties() {
        return App.PROPERTIES;
    }

    /**
     * @return Returns the object used to store employees
     */
    public static EmployeesStorage getEmployeesStorage() {
        return DATABASE_INSTANCE;
    }

    /**
     * @return Returns the object used to store departments
     */
    public static DepartmentsStorage getDepartmentsStorage() {
        return DATABASE_INSTANCE;
    }

    /**
     * @return Returns the object used to store viewers
     */
    public static ViewersStorage getViewersStorage() {
        return DATABASE_INSTANCE;
    }

    /**
     * @return Returns the object used to store offline streamers
     */
    public static OfflineStreamersStorage getOfflineStreamersStorage() {
        return DATABASE_INSTANCE;
    }

    /**
     * @return Returns the object used to store admin's credential
     */
    public static AdminStorage getAdminStorage() {
        return DATABASE_INSTANCE;
    }

    public static SecureConfigure getSecureConfigure() { return  DATABASE_INSTANCE; }
}
