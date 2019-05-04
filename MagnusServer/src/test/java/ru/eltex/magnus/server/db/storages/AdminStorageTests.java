package ru.eltex.magnus.server.db.storages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.TestStoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Admin;

import static org.junit.jupiter.api.Assertions.*;

class AdminStorageTests {
    @Test
    void testAdminUpdate() {
        AdminStorage storage = TestStoragesProvider.getAdminStorage();
        Admin backup = storage.getAdmin();

        Admin a = new Admin(backup.getLogin(), backup.getPassword());
        a.setLogin("supervisor_dominator");
        a.setPassword("12345");
        assertTrue(storage.updateAdmin(a));
        Admin a2 = storage.getAdmin();
        assertEquals(a, a2);

        storage.updateAdmin(backup);
    }
}
