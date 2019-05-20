package ru.eltex.magnus.server.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger(ImageController.class);

    @GetMapping("/screenshot")
    public ResponseEntity<byte[]> getScreenshot(@RequestParam("login") String login) {
        StreamerRequester streamerRequester = StreamersServer.getStreamerByLogin(login);
        if (streamerRequester == null) {
            LOG.warn("Failed to get streamerRequester");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        byte[] screenshot = streamerRequester.getScreenshot();
        if(screenshot == null) {
            LOG.warn("Failed to get screenshot: unknown error");
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