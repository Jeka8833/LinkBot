package com.Jeka8833.HelloSite;

import com.Jeka8833.LinkBot.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Site {
    private static final Logger LOGGER = LogManager.getLogger(Site.class);
    private static final AtomicInteger tickCount = new AtomicInteger();
    private static volatile long nextTime = 0;


    private final int threadCount;
    private final ExecutorService executorService;
    private final URL[] siteList;

    public Site(int threadCount, URL[] siteList) {
        this.threadCount = threadCount;
        this.siteList = siteList;

        executorService = Executors.newFixedThreadPool(threadCount);
    }

    public void start() {
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(new Worker(siteList[(i * siteList.length) / threadCount]));
        }
    }

    public static void main(String[] args) {
        URL[] sites = Arrays.stream(Util.getParam(args, "-sites").split(";")).map(s -> {
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                return null;
            }
        }).filter(Objects::nonNull).toArray(URL[]::new);
        LOGGER.info("Sites:");
        for (URL url : sites) {
            LOGGER.info(url);
        }
        new Site(Integer.parseInt(Util.getParam(args, "-threads")), sites).start();
    }

    public record Worker(URL url) implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
                    connection.setDoInput(true);
                    connection.setConnectTimeout(10_000);           // 10 second
                    connection.setReadTimeout(10_000);              // 10 second
                    (connection.getResponseCode() == 200 ?
                            connection.getInputStream() : connection.getErrorStream()).readAllBytes();     //Block thread
                } catch (Exception ignored) {
                }
                if (nextTime < System.currentTimeMillis()) {
                    nextTime = System.currentTimeMillis() + 30 * 60 * 1000; // 30 Minutes
                    LOGGER.info("Ticks: " + tickCount.get());
                }
                tickCount.incrementAndGet();
            }
        }
    }
}
