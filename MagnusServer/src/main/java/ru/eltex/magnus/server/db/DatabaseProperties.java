package ru.eltex.magnus.server.db;

interface DatabaseProperties {
    String getConnectionURL();
    String getLogin();
    String getPassword();
}
