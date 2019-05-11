package ru.eltex.magnus.server.db.dataclasses;

import java.util.Objects;

/**
 * Class containing the admin's credential: login and password
 */
public class Admin {
    private String login;
    private String password;

    /**
     * Allocates new {@link Admin} object with null login and password
     */
    public Admin() { }

    /**
     * Allocates new {@link Admin} object with specified login and password
     * @param login the login of the admin
     * @param password the password of the admin
     */
    public Admin(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(login, admin.login) &&
                Objects.equals(password, admin.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
