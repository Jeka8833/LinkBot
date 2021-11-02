package com.Jeka8833.TntCommunity.packet;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.packets.*;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;
import java.util.UUID;

public class PacketManager {

    public static final BiMap<Byte, Class<? extends Packet>> packetsList = new BiMap<>();

    static {
        packetsList.put((byte) 1, ActiveModulesPacket.class);
        packetsList.put((byte) 2, AuthPacket.class);
        packetsList.put((byte) 3, PingPacket.class);
        packetsList.put((byte) 4, RequestPlayerStatusPacket.class);
        packetsList.put((byte) 5, SendPlayerStatusPacket.class);
    }

    public static void serverParse(final WebSocket socket, final ByteBuffer buffer) {
        try (final PacketInputStream stream = new PacketInputStream(buffer)) {
            final byte index = stream.readByte();
            final TNTUser user = TNTUser.keyUserList.getOrDefault((UUID) socket.getAttachment(), null);
            if (index != 1 && user == null) { // index 1 - Auth Packet
                socket.close(1, "Incorrect packet sequence.");
                return;
            }
            final Packet packet = packetsList.get(index).getDeclaredConstructor().newInstance();
            packet.read(stream);
            packet.serverProcess(socket, user);
            if (user != null)
                user.heartBeat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverSend(final WebSocket socket, final Packet packet) {
        try (final PacketOutputStream stream = new PacketOutputStream()) {
            packet.write(stream);
            socket.send(stream.getByteBuffer(packet.getClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
