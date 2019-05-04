package ru.eltex.magnus.server.db.storages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.eltex.magnus.server.db.TestStoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Viewer;

import static org.junit.jupiter.api.Assertions.*;

class ViewersStorageTests {
    @BeforeAll
    @AfterAll
    static void cleanup() {
        ViewersStorage storage = TestStoragesProvider.getViewersStorage();
        for(Viewer v : storage.getAllViewers()) {
            storage.removeViewerByLogin(v.getLogin());
        }
    }

    @Test
    void testInsertGetUpdateRemoveViewer() {
        ViewersStorage storage = TestStoragesProvider.getViewersStorage();

        Viewer v = new Viewer("asdf", "zxczfy", "Anakin");
        testInsertGetUpdateRemoveViewer(storage, v);

        v = new Viewer("kljasfd", "kjasnfd", "Leia");
        testInsertGetUpdateRemoveViewer(storage, v);

        v = new Viewer("aosdif09", "xzlkc891", "Father");
        testInsertGetUpdateRemoveViewer(storage, v);
    }

    void testInsertGetUpdateRemoveViewer(ViewersStorage storage, Viewer v) {
        assertTrue(storage.insertViewer(v));
        Viewer v2 = storage.getViewerByLogin(v.getLogin());
        assertEquals(v, v2);

        v.setName(v.getName() + " Bob");
        assertTrue(storage.updateViewer(v));
        v2 = storage.getViewerByLogin(v.getLogin());
        assertEquals(v, v2);

        assertTrue(storage.removeViewerByLogin(v.getLogin()));

        assertFalse(storage.getAllViewers().contains(v));
    }
}
