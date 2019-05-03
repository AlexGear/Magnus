package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;

import java.util.List;

public interface OfflineStreamersStorage {
    List<OfflineStreamer> getAllOfflineStreamers();
    OfflineStreamer getOfflineStreamerByLogin(String login);
    boolean insertOfflineStreamer(OfflineStreamer offlineStreamer);
    boolean updateOfflineStreamer(OfflineStreamer offlineStreamer);
    boolean removeOfflineStreamerByLogin(String login);
}
