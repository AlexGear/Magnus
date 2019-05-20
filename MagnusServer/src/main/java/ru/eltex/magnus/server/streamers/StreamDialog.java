package ru.eltex.magnus.server.streamers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class StreamDialog {

    private static final Logger LOG = LogManager.getLogger(StreamerRequester.class);
    private static final int MAX_BUFFER_SIZE = 2 << 18;

    public static synchronized void sendMessage(DataOutputStream outputStream, byte[] data) throws IOException {
        outputStream.writeInt(data.length);
        outputStream.write(data);
        outputStream.flush();
    }

    public static synchronized byte[] getMessage(DataInputStream inputStream) throws IOException {
        int size = inputStream.readInt();
        if (size < 0 || size > MAX_BUFFER_SIZE) {
            LOG.warn("Unacceptable message size: " + size);
            return null;
        }
        byte[] buffer = new byte[size];
        inputStream.readFully(buffer, 0, size);
        return buffer;
    }
}
