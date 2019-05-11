package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;

import java.util.List;

/**
 * Represents an abstract storage of information about streamers being offline
 */
public interface OfflineStreamersStorage {
    /**
     * Gets all the offline streamers stored in the storage
     * @return {@link List} of {@link OfflineStreamer}s stored in the storage
     */
    List<OfflineStreamer> getAllOfflineStreamers();

    /**
     * Searches for the offline streamer in the storage by its login
     * @param login the login of the {@link OfflineStreamer} to find
     * @return {@link OfflineStreamer} object if it was found, otherwise {@literal null}
     */
    OfflineStreamer getOfflineStreamerByLogin(String login);

    /**
     * Insert a new offline streamer record into the storage
     * @param offlineStreamer {@link OfflineStreamer} object to insert into the storage
     * @return {@literal true} if inserted successfully and {@literal false} if an error occurred
     * or an offline streamer with the same login is already contained in the storage
     */
    boolean insertOfflineStreamer(OfflineStreamer offlineStreamer);

    /**
     * Searches for the offline streamer with the same login in the storage and updates its fields
     * @param offlineStreamer {@link OfflineStreamer} object whose fields are used to perform the update
     * @return {@literal true} if updated successfully and {@literal false} if an error occurred
     * or the offline streamer was not found
     */
    boolean updateOfflineStreamer(OfflineStreamer offlineStreamer);

    /**
     * Searches for the offline streamer with specified login and removes it from the storage
     * @param login the login of the {@link OfflineStreamer} that should be removed
     * @return {@literal true} if removed successfully and {@literal false} if an error occurred
     * or the offline streamer was not found
     */
    boolean removeOfflineStreamerByLogin(String login);
}
