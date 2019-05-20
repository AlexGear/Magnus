package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.StorageException;
import ru.eltex.magnus.server.db.dataclasses.Viewer;

import java.util.List;

/**
 * Represents an abstract storage of {@link Viewer}s
 */
public interface ViewersStorage {
    /**
     * Get all the viewers contained in the storage
     * @return {@link List} of {@link Viewer}s stored in the storage
     */
    List<Viewer> getAllViewers() throws StorageException;

    /**
     * Searches for the viewer by its login in the storage and returns it
     * @param login the login of the {@link Viewer} to find
     * @return {@link Viewer} object if it was found, otherwise {@literal null}
     */
    Viewer getViewerByLogin(String login) throws StorageException;

    /**
     * Inserts new viewer into the storage
     * @param viewer the {@link Viewer} object to insert into the storage
     * @return {@literal true} if added successfully and {@literal false} if an error occurred or
     * a viewer with the same login is already contained in the storage
     */
    boolean insertViewer(Viewer viewer) throws StorageException;

    /**
     * Searches for the viewer by its login in the storage and updates its values
     * @param viewer {@link Viewer} object whose values are used to perform the update
     * @return {@literal true} if updated successfully and {@literal false} if an error occurred or
     * the viewer was not found
     */
    boolean updateViewer(Viewer viewer) throws StorageException;

    /**
     * Searches for the viewer by its login in the storage and removes it from the storage
     * @param login the login of the {@link Viewer} that should be removed
     * @return {@literal true} if removed successfully and {@literal false} if an error occurred or
     * the viewer was not found
     */
    boolean removeViewerByLogin(String login) throws StorageException;
}
