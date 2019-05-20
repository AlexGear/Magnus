package ru.eltex.magnus.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.eltex.magnus.server.streamers.StreamersServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class App {
    private static final String DEFAULT_PROPERTIES_FILENAME = "magnus.properties";

    private static final Logger LOG = LogManager.getLogger(App.class);

    public static final AppProperties PROPERTIES;

    static {
        String propertiesFilename = getPropertiesFilename();
        try {
            PROPERTIES = new AppProperties(propertiesFilename);
        } catch (IOException e) {
            String msg = "Failed to load properties from '" + propertiesFilename + "'";
            LOG.fatal(msg);
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
        final String systemProperty = "propertiesFile";
        String filename = System.getProperty(systemProperty);
        if(filename != null && !filename.isEmpty()) {
            LOG.info("System property 'propertiesFile'='" + filename + "'. Using it.");
            return filename;
        }
        LOG.info("System property 'propertiesFile' not set. Using default filename: " + DEFAULT_PROPERTIES_FILENAME + "'");
        return DEFAULT_PROPERTIES_FILENAME;
    }

    public static void main(String[] args) throws IOException {
//        EmployeesStorage storage = StoragesProvider.getEmployeesStorage();
//        for(Employee e: storage.getAllEmployees()) {
//            e.setPassword(new BCryptPasswordEncoder().encode(e.getPassword()));
//            storage.updateEmployee(e);
//        }
//        AdminStorage storage = StoragesProvider.getAdminStorage();
//        Admin a = storage.getAdmin();
//        a.setPassword(new BCryptPasswordEncoder().encode(a.getPassword()));
//        storage.updateAdmin(a);

        ApplicationContext context = SpringApplication.run(App.class, args);
        StreamersServer.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (!"exit".equals(in.readLine().trim().toLowerCase())) ;
        }
        SpringApplication.exit(context);
    }
}