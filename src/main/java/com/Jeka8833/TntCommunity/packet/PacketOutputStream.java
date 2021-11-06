package com.Jeka8833.TntCommunity.packet;

import com.Jeka8833.TntCommunity.Server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class PacketOutputStream extends DataOutputStream {
    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter {@code written} is
     * set to zero.
     *
     * @see FilterOutputStream#out
     */
    public PacketOutputStream() {
        super(new ByteArrayOutputStream());
    }

    public void writeUUID(final UUID uuid) throws IOException {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public ByteBuffer getByteBuffer(final Class<? extends Packet> type) {
        final byte[] arr = ((ByteArrayOutputStream) this.out).toByteArray();
        return ByteBuffer.allocate(arr.length + 1)
                .put(Server.packetsList.getKey(type))
                .put(arr, 1, arr.length);
    }
}
