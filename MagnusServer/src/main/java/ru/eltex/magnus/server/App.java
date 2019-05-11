package ru.eltex.magnus.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;
import ru.eltex.magnus.server.streamers.StreamersServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class App {
    private static final String DEFAULT_PROPERTIES_FILENAME = "magnus.properties";
    public static final AppProperties PROPERTIES;

    static {
        String propertiesFilename = getPropertiesFilename();
        try {
            PROPERTIES = new AppProperties(propertiesFilename);
        } catch (IOException e) {
            String msg = "Failed to load properties from '" + propertiesFilename + "'";
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

    public static void main(String[] args) throws IOException {
//        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
//        for(Employee e: storage.getAllEmployees()) {
//            e.setPassword(new BCryptPasswordEncoder().encode(e.getPassword()));
//            storage.updateEmployee(e);
//        }

        ApplicationContext context = SpringApplication.run(App.class, args);
        System.setProperty("java.awt.headless", "false");
        StreamersServer.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (!"exit".equals(in.readLine().trim().toLowerCase())) ;
        }
        SpringApplication.exit(context);
    }
}