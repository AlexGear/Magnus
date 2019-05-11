package ru.eltex.magnus.server.db.dataclasses;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * Class representing information about an offline streamer:
 * login and last time ({@link Timestamp}) it was online.<br>
 * <b>NOTE:</b><br>
 * The method {@link OfflineStreamer#equals(Object)} checks {@link OfflineStreamer#lastSeen}
 * fields for an approximate match with tolerance 1000 ms. That was made because
 * a MySQL database tends to spoil milliseconds of the provided {@link Timestamp} value so
 * exact equality checks were failing tests.
 */
public class OfflineStreamer {
    private String login;
    private Timestamp lastSeen;

    /**
     * Allocates a new {@link OfflineStreamer} object with null login and lastSeen fields
     */
    public OfflineStreamer() { }

    /**
     * Allocates a new {@link OfflineStreamer} object with specified login and lastSeen fields
     * @param login the login of the streamer became offline
     * @param lastSeen the last time it was seen online
     */
    public OfflineStreamer(String login, Timestamp lastSeen) {
        this.login = login;
        this.lastSeen = lastSeen;
    }

    /**
     * Creates a new {@link OfflineStreamer} object with specified login and lastSeen field set to current time
     * @param login the login of the streamer became offline
     * @return the created {@link OfflineStreamer} instance
     */
    public static OfflineStreamer forCurrentTime(String login) {
        return new OfflineStreamer(login, Timestamp.from(Instant.now()));
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

    /**
     * This method checks {@link OfflineStreamer#login} fields for exact match and
     * {@link OfflineStreamer#lastSeen} fields for an approximate match with
     * tolerance 1000 ms. That was made because a MySQL database tends
     * to spoil milliseconds of the provided {@link Timestamp} value so
     * exact equality checks were failing tests.
     * @param o other object to compare equality to
     * @return {@literal true} if the objects are equal, otherwise {@literal false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfflineStreamer that = (OfflineStreamer) o;
        return Objects.equals(login, that.login) &&
                timestampsApproximateEquals(lastSeen, that.lastSeen, 1000);
    }

    /**
     * Checks two {@link Timestamp}s for approximate equality with specified tolerance
     * @param a the first {@link Timestamp}
     * @param b the second {@link Timestamp}
     * @param millisTolerance the tolerance that {@link Timestamp}s a and b may differ from each other by
     *                        and still be considered equal
     * @return @return {@literal true} if the objects are equal, otherwise {@literal false}
     */
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
