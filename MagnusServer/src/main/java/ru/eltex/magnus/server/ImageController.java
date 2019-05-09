package ru.eltex.magnus.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;

@RestController
public class ImageController {
    @GetMapping("screenshot")
    public byte[] getScreenshot() throws AWTException, IOException {
        StreamerRequester streamerRequester = StreamersServer.getStreamerReqByLogin("1");
        return streamerRequester.getScreenshot();
    }
}
