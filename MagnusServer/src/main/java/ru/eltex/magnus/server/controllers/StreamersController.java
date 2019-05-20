package ru.eltex.magnus.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.magnus.server.StorageException;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Employee;
import ru.eltex.magnus.server.db.dataclasses.OfflineStreamer;
import ru.eltex.magnus.server.db.storages.EmployeesStorage;
import ru.eltex.magnus.server.db.storages.OfflineStreamersStorage;
import ru.eltex.magnus.server.streamers.StreamerRequester;
import ru.eltex.magnus.server.streamers.StreamersServer;

import java.util.ArrayList;
import java.util.List;


@RestController
public class StreamersController {
    public static class StreamerInfo {
        public final String login, name, department, jobName, phoneNumber, email;

        public StreamerInfo(Employee src) {
            login = src.getLogin();
            name = src.getName();
            department = src.getDepartment().getName();
            jobName = src.getJobName();
            phoneNumber = src.getPhoneNumber();
            email = src.getEmail();
        }
    }
    public static class OfflineStreamerInfo {
        public final StreamerInfo streamerInfo;
        public final String lastSeenTimeAgo;

        public OfflineStreamerInfo(StreamerInfo streamerInfo, String lastSeenTimeAgo) {
            this.streamerInfo = streamerInfo;
            this.lastSeenTimeAgo = lastSeenTimeAgo;
        }
    }
    public static class StreamersListInfo {
        public final List<StreamerInfo> onlineStreamers;
        public final List<OfflineStreamerInfo> offlineStreamers;

        public StreamersListInfo(List<StreamerInfo> onlineStreamers, List<OfflineStreamerInfo> offlineStreamers) {
            this.onlineStreamers = onlineStreamers;
            this.offlineStreamers = offlineStreamers;
        }
    }

    @GetMapping("/get_streamers_list")
    public StreamersListInfo getStreamersList() {
        EmployeesStorage employeesStorage = StoragesProvider.getEmployeesStorage();
        OfflineStreamersStorage offlineStreamersStorage = StoragesProvider.getOfflineStreamersStorage();

        List<StreamerInfo> onlineInfos = new ArrayList<>();
        List<OfflineStreamerInfo> offlineInfos = new ArrayList<>();
        try {
            for (StreamerRequester streamer : StreamersServer.getAllStreamers()) {
                String login = streamer.getLogin();
                Employee employee = employeesStorage.getEmployeeByLogin(login);
                onlineInfos.add(new StreamerInfo(employee));
            }

            for (OfflineStreamer streamer : offlineStreamersStorage.getAllOfflineStreamers()) {
                String lastSeen = streamer.getLastSeenTimeAgo();
                String login = streamer.getLogin();
                Employee employee = employeesStorage.getEmployeeByLogin(login);
                offlineInfos.add(new OfflineStreamerInfo(new StreamerInfo(employee), lastSeen));
            }
        } catch (StorageException e){
            e.printStackTrace();
        }
        return new StreamersListInfo(onlineInfos, offlineInfos);
    }

    @RequestMapping("/send_message")
    public ResponseEntity<String> sendMessage(@RequestParam("login") String login, @RequestParam("message") String message) {
        StreamerRequester streamerRequester = StreamersServer.getStreamerByLogin(login);
        if (streamerRequester == null) {
            String body = "User with login '" + login + "' not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        streamerRequester.sendNotification(message);
        return ResponseEntity.ok("OK");
    }
}
