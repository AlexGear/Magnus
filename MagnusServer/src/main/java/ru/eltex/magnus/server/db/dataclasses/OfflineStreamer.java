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
                timestampsApproximateEquals(lastSeen, that.lastSeen, 1000);
    }

    private static boolean timestampsApproximateEquals(Timestamp a, Timestamp b, int millisTolerance) {
        return a == b || a != null && b != null
                && Math.abs(a.getTime() - b.getTime()) <= millisTolerance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, lastSeen);
    }

    @Override
    public String toString() {
        return "OfflineStreamer{" +
                "login='" + login + '\'' +
                ", lastSeen=" + lastSeen +
                '}';
    }
}
