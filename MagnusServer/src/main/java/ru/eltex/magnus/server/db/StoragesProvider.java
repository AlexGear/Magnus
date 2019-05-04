package ru.eltex.magnus.server.db;

import ru.eltex.magnus.server.db.storages.*;

import java.io.IOException;

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
    private static final String DEFAULT_PROPERTIES_FILENAME = "magnus.properties";
    private static final Database DATABASE_INSTANCE;

    static {
        String propertiesFilename = getPropertiesFilename();
        try {
            DatabaseProperties properties = new DatabaseFileProperties(propertiesFilename);
            DATABASE_INSTANCE = new Database(properties);
        } catch (IOException e) {
            String msg = "Failed to load DB properties from '" + propertiesFilename + "'";
            throw new Error(msg, e);
        }
    }

    /**
     * Gets the filename of the properties file. If 'propertiesFile' system property
     * is not empty then its value returned, otherwise returns
     * the default value {@value DEFAULT_PROPERTIES_FILENAME}
     * @return the filename of the properties file
     */
    private static String getPropertiesFilename() {
        String filename = System.getProperty("propertiesFile");
        if(filename != null && !filename.isEmpty()) {
            return filename;
        }
        return DEFAULT_PROPERTIES_FILENAME;
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
}
