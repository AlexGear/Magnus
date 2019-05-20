package ru.eltex.magnus.server.db.dataclasses;

import java.util.Objects;

/**
 * Class containing information about department: id, name
 */
public class Department {
    private int id;
    private String name;

    /**
     * Allocates new {@link Department} object with id = 0 and name = null
     */
    public Department() { }

    /**
     * Allocates new {@link Department} object with specified id and name
     * @param id the id of the department
     * @param name the name of the department
     */
    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        Department that = (Department) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
