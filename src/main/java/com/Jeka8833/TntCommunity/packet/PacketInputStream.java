package com.Jeka8833.TntCommunity.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public class PacketInputStream extends DataInputStream {

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public PacketInputStream(final InputStream in) {
        super(in);
    }

    public PacketInputStream(final ByteBuffer buffer) {
        super(new ByteArrayInputStream(buffer.array()));
        if (buffer.array().length > 8 * 1024) // 8 KB
            System.out.println();
    }

    public UUID readUUID() throws IOException {
        return new UUID(readLong(), readLong());
    }
}
