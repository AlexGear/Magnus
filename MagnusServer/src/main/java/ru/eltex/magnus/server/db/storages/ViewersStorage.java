package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Viewer;

import java.util.List;

public interface ViewersStorage {
    Viewer getViewerByLogin(String login);
    List<Viewer> getAllViewers();
    boolean insertViewer(Viewer viewer);
    boolean updateViewer(Viewer viewer);
    boolean removeViewerByLogin(String login);
}
