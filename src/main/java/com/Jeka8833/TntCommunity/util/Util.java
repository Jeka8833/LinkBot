package com.Jeka8833.TntCommunity.util;

import com.Jeka8833.LinkBot.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class Util {

    private static final Logger logger = LogManager.getLogger(Util.class);

    public static final long DJFix = 1L << 6;

    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean checkKey(final UUID user, final UUID key) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.hypixel.net/key?key=" + key)).GET().build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200)
                return Main.GSON.fromJson(response.body(), Util.ParseUser.class).record.owner.equals(user);
            else
                logger.warn("User not valid test: " + "https://api.hypixel.net/key?key=" + key);
        } catch (Exception ex) {
            logger.warn("Hypixel API is down", ex);
        }
        return false;
    }

    private static class ParseUser {
        private Util.Record record;
    }

    private static class Record {
        private UUID owner;
    }

}
