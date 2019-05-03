package ru.eltex.magnus.server.db.dataclasses;

import java.sql.Timestamp;
import java.util.Objects;

public class OfflineStreamer {
    private String login;
    private Timestamp lastSeen;

    public OfflineStreamer() { }

    public OfflineStreamer(String login, Timestamp lastSeen) {
        this.login = login;
        this.lastSeen = lastSeen;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
        return Objects.equals(login, that.login) &&
                Objects.equals(lastSeen, that.lastSeen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, lastSeen);
    }
}
