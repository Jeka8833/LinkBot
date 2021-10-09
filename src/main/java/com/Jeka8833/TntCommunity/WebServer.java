package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Main;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServer extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new WebServer());
    }

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
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
            response.putHeader("content-type", "application/json");
            try {
                Main.db.statement.executeQuery("SELECT * FROM \"LB_Users\"");
                response.end("{\"status\": \"good\"}");
            } catch (Exception e) {
                response.end("{\"status\": \"" + e.getMessage() + "\"}");
            }
        });
        router.route("/hello").handler(event -> {
            HttpServerResponse response = event.response();
            response.end("{\"status\":");
        });
        server.requestHandler(router).listen(8080);
    }
}