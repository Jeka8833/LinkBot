package com.Jeka8833.TntCommunity.page;

import com.Jeka8833.TntCommunity.WebServer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class AuthPage implements Page {

    @Override
    public void init(Router router) {
        router.route(HttpMethod.POST, "/auth").handler(rc -> {
            final HttpServerResponse response = rc.response();
            try {
                final JsonObject json = rc.getBodyAsJson(256);
                if (json == null) {
                    response.setStatusCode(400);
                    return;
                }

                final String version = json.getString("version");
                if (version == null || version.isEmpty() || version.length() > 48) {
                    response.setStatusCode(400);
                    return;
                }
                final UUID user = UUID.fromString(json.getString("user"));
                final UUID key = UUID.fromString(json.getString("key"));
                if (isLoginSuccess(user, key)) {
                    response.setStatusCode(200);
                } else {
                    response.setStatusCode(401);
                }
            } catch (Exception ex) {
                response.setStatusCode(500);
            }
        });
    }

    private static boolean isLoginSuccess(final UUID user, final UUID key) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.hypixel.net/key?key=" + key)).GET().build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            return switch (response.statusCode()) {
                case 200, 429 -> WebServer.gson.fromJson(response.body(), ParseUser.class).record.owner.equals(user);
                default -> false;
            };
        } catch (IOException | InterruptedException ignored) {
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
