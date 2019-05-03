package ru.eltex.magnus.server.db.storages;

import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;

import java.util.List;

public interface OfflineStreamersStorage {
    OfflineStreamer getOfflineStreamerByLogin(String login);
    List<OfflineStreamer> getAllOfflineStreamers();
}
