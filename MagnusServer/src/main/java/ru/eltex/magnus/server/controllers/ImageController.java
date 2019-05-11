package ru.eltex.magnus.server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.magnus.server.streamers.StreamerRequester;
import ru.eltex.magnus.server.streamers.StreamersServer;

@RestController
public class ImageController {
    @GetMapping("screenshot")
    public byte[] takeScreenshot() {
        StreamerRequester streamerRequester = StreamersServer.getStreamerByLogin("1");
        if (streamerRequester != null) {
            return streamerRequester.takeScreenshot();
        }
        return new byte[0];
    }
}