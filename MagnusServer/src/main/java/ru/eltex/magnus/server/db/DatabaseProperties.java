package ru.eltex.magnus.server.db;

/**
 * Represents properties provider needed to establish connection to a database
 */
public interface DatabaseProperties {
    /**
     * @return the connection URL used to connect to the database
     */
    String getConnectionURL();

    /**
     * @return the login of the database user
     */
    String getLogin();

    /**
     * @return the password of the database user
     */
    String getPassword();
}
