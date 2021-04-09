package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Util;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TntCommunity implements Runnable {

    private final String key;

    public TntCommunity(final String key) {
        this.key = key;
    }

    public static void main(String[] args) {
        final Thread thread = new Thread(new TntCommunity(Util.getParam(args, "-tntkey")));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        final CloseableHttpClient client = HttpClients.createDefault();
        final HttpPost httppost = new HttpPost("http://tntclientv2.000webhostapp.com/player/updatestats.php?key=" + key);
        while (true) {
            try {
                final int minute = (int) ((System.currentTimeMillis() / (1000 * 60)) % 60);
                if (minute == 0 || minute == 30)
                    client.execute(httppost);
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
