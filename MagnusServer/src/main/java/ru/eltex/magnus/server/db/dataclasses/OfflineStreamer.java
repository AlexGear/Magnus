package ru.eltex.magnus.server.db.dataclasses;

import java.sql.Timestamp;
import java.util.Objects;

public class OfflineStreamer {
    private Employee employee;
    private Timestamp lastSeen;

    public OfflineStreamer() { }

    public OfflineStreamer(Employee employee, Timestamp lastSeen) {
        this.employee = employee;
        this.lastSeen = lastSeen;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfflineStreamer that = (OfflineStreamer) o;
        return Objects.equals(employee, that.employee) &&
                Objects.equals(lastSeen, that.lastSeen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, lastSeen);
    }
}
