package ru.eltex.magnus.streamer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class ScreenshotMaker {
    private static final Logger LOG = LogManager.getLogger(ScreenshotMaker.class);
    private static final Robot ROBOT = createRobot();

    private static Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            String message = "Failed to create Robot instance for screenshot making: " + e.toString();
            LOG.fatal(message);
            throw new UnsupportedOperationException(message, e);
        }
    }

    static byte[] takeScreenshot() throws IOException {
        Rectangle rect = new Rectangle(calculateScreenBounds());
        BufferedImage image = ROBOT.createScreenCapture(rect);
        return compress(image, 0.2f);
    }

    private static byte[] compress(BufferedImage image, float scale) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos))
        {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(scale);

            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
            byte[] data = baos.toByteArray();

            writer.dispose();

            return data;
        }
    }

    private static Dimension calculateScreenBounds() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        Dimension allScreenBounds = new Dimension();
        for (GraphicsDevice screen : screens) {
            Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();

            allScreenBounds.width += screenBounds.width;
            allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
        }
        return allScreenBounds;
    }
}
