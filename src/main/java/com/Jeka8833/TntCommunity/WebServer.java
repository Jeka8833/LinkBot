package com.Jeka8833.TntCommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebServer {

    public static void main(String[] args) {
        final Thread thread = new Thread(() -> SpringApplication.run(WebServer.class, args));
        thread.setDaemon(true);
        thread.start();
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);

    }
}