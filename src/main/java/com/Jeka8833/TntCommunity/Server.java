package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import com.Jeka8833.TntCommunity.packet.packets.*;
import com.Jeka8833.TntCommunity.util.BiMap;
import com.Jeka8833.dataBase.TNTClientDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.UUID;


public class Server extends WebSocketServer {

    private static final Logger logger = LogManager.getLogger(Server.class);

    public static final BiMap<Byte, Class<? extends Packet>> packetsList = new BiMap<>();

    public static Server server;

    static {
        packetsList.put((byte) 1, ActiveModulesPacket.class);
        packetsList.put((byte) 2, AuthPacket.class);
        packetsList.put((byte) 3, PingPacket.class);
        packetsList.put((byte) 4, RequestPlayerStatusPacket.class);
        packetsList.put((byte) 5, SendPlayerStatusPacket.class);
        packetsList.put((byte) 6, ChatPacket.class);
        packetsList.put((byte) 7, BlockModulesPacket.class);
        packetsList.put((byte) 8, GameInfoPacket.class);
        packetsList.put((byte) 9, FightPacket.class);

    }

    public Server(final InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        try (final PacketInputStream stream = new PacketInputStream(message)) {
            final TNTUser user = TNTUser.keyUserList.get((UUID) conn.getAttachment());
            if (user == null && !(stream.packet instanceof AuthPacket)) {
                conn.close(1, "Incorrect packet sequence.");
            } else {
                stream.packet.read(stream);
                stream.packet.serverProcess(conn, user);
                if (user != null)
                    user.heartBeat();
            }
        } catch (Exception e) {
            logger.error("Fail parse packet", e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("Server have a error:", ex);
    }

    @Override
    public void onStart() {
        // Ping every 15 second and wait answer 23 second
        setConnectionLostTimeout(15);
    }

    public static void serverSend(final WebSocket socket, final Packet packet) {
        try (final PacketOutputStream stream = new PacketOutputStream()) {
            packet.write(stream);
            socket.send(stream.getByteBuffer(packet.getClass()));
        } catch (Exception e) {
            logger.error("Fail send packet:", e);
        }
    }

    public static void serverBroadcast(final Packet packet) {
        try (final PacketOutputStream stream = new PacketOutputStream()) {
            packet.write(stream);
            server.broadcast(stream.getByteBuffer(packet.getClass()));
        } catch (Exception e) {
            logger.error("Fail send packet:", e);
        }
    }

    public static void main(String[] args) {
        try {
            //DatabaseManager.initConnect(Util.getParam(args, "-db_url"), Util.getParam(args, "-db_user"),
            //        Util.getParam(args, "-db_pass"));
            server = new Server(new InetSocketAddress(Integer.parseInt(Util.getParam(args, "-port"))));
            server.start();
            TNTClientDB.init();
        } finally {
            TNTUser.keyUserList.values().stream()
                    .filter(tntUser -> tntUser.key != null && tntUser.version != null)
                    .forEach(TNTClientDB.writeList::add);
            try {
                TNTClientDB.writeUser();
            } catch (SQLException e) {
                logger.error("Fail write users:", e);
            }
        }
    }
}
