package ru.eltex.magnus.server.db.dataclasses;

import java.util.Objects;

/**
 * Class containing a viewer's credential (login and password) and name
 */
public class Viewer {
    private String login;
    private String password;
    private String name;

    /**
     * Allocates a new {@link Viewer} object with null login, password and name
     */
    public Viewer() { }

    /**
     * Allocates a new {@link Viewer} object with specified login, password and name
     * @param login the viewer's login
     * @param password the viewer's password
     * @param name the viewer's name
     */
    public Viewer(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Viewer viewer = (Viewer) o;
        return Objects.equals(login, viewer.login) &&
                Objects.equals(password, viewer.password) &&
                Objects.equals(name, viewer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, name);
    }

    @Override
    public String toString() {
        return "Viewer{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
