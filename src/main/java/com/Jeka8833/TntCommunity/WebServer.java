package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Util;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class WebServer extends AbstractVerticle {

    public static final Gson gson = new Gson();

    private final int port;

    public WebServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new WebServer(Integer.parseInt(Util.getParam(args, "-port"))));
    }

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create().setBodyLimit(1024L * 1024L)); // MB

        router.route(HttpMethod.POST, "/lol").handler(rc -> {
            JsonObject json = rc.getBodyAsJson();
            System.out.println(json.getString("id"));
            HttpServerResponse response = rc.response();
            response.putHeader("content-type", "application/json");

            // Write to the response and end it
            response.end("{\"status\": 200}");
        });
        router.route("/kek").handler(rc -> {
            HttpServerResponse response = rc.response();
            response.putHeader("content-type", "text/event-stream");
            for (int i = 0; i < 100; i++) {
                try {
                    response.write("Test 1");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        router.route("/hello").handler(event -> {
            HttpServerResponse response = event.response();
            response.end("{\"status\":");
        });
        server.requestHandler(router).listen(port);
    }
}