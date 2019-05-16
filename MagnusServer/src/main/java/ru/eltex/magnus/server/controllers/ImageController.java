package ru.eltex.magnus.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.magnus.server.streamers.StreamerRequester;
import ru.eltex.magnus.server.streamers.StreamersServer;

@RestController
public class ImageController {
    @GetMapping("/screenshot")
    public ResponseEntity<byte[]> getScreenshot(@RequestParam("login") String login) {
        StreamerRequester streamerRequester = StreamersServer.getStreamerByLogin(login);
        if (streamerRequester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        byte[] screenshot = streamerRequester.getScreenshot();
        if(screenshot == null) {
            final int unknownError = 520;
            return ResponseEntity.status(unknownError).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .header("Pragma-directive", "no-cache")
                .header("Cache-directive", "no-cache")
                .header("Cache-control", "no-cache")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(screenshot);
    }
}