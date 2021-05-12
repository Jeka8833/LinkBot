package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;

public class TntCommunity implements Runnable {

    private final String keyApi;
    private final String keyVisual;

    public TntCommunity(final String keyApi, String keyVisual) {
        this.keyApi = keyApi;
        this.keyVisual = keyVisual;
    }

    public static void main(String[] args) {
        final Thread thread = new Thread(new TntCommunity(Util.getParam(args, "-keyapi"), Util.getParam(args, "-keyvisual")));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        final CloseableHttpClient client = HttpClients.createDefault();
        while (true) {
            try {
                final int minute = (int) ((System.currentTimeMillis() / (1000 * 60)) % 60);
                if (minute == 0 || minute == 30) {
                    client.execute(new HttpPost("http://tntclientv2.000webhostapp.com/player/updatestats.php?key=" + keyApi));

                    String res = "{\"key\":\"" + keyVisual + "\"}";
                    while (!res.equals("")) {
                        HttpPost httppost = new HttpPost("https://tntstats.000webhostapp.com/update.php");

                        StringEntity params = new StringEntity(res);
                        httppost.addHeader("content-type", "application/x-www-form-urlencoded");
                        httppost.setEntity(params);

                        HttpResponse response = client.execute(httppost);
                        HttpEntity entity = response.getEntity();

                        if (entity != null) {
                            try (InputStream instream = entity.getContent()) {
                                res = Util.readInputStream(instream);
                            }
                        }
                    }
                }
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
