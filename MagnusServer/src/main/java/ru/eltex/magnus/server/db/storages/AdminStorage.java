package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Admin;

/**
 * Represents an abstract storage of {@link Admin}'s credential
 */
public interface AdminStorage {
    /**
     * @return Returns the stored {@link Admin}'s credential
     */
    Admin getAdmin();

    /**
     * Changes the stored {@link Admin}'s credential to the new value
     * @param admin the value to set the {@link Admin}'s credential to
     * @return {@literal} true if update has been performed successfully, otherwise {@literal false}
     */
    boolean updateAdmin(Admin admin);
}
