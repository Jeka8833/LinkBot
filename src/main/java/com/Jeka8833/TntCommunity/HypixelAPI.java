package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class HypixelAPI {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean checkKey(final UUID user, final UUID key) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.hypixel.net/key?key=" + key)).GET().build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200)
                return Main.gson.fromJson(response.body(), ParseUser.class).record.owner.equals(user);
        } catch (Exception ignored) {
        }
        return false;
    }

    private static class ParseUser {
        private Record record;
    }

    private static class Record {
        private UUID owner;
    }
}
