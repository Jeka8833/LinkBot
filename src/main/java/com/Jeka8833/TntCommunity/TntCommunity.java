package com.Jeka8833.TntCommunity;

import com.Jeka8833.LinkBot.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
                    String res = "{\"key\":\"" + keyApi + "\"}";
                    while (!res.equals("")) {
                        res = updateSite("\"http://tntclientv2.000webhostapp.com/player/updatestats.php", res, client);
                    }

                    res = "{\"key\":\"" + keyVisual + "\"}";
                    while (!res.equals("")) {
                        res = updateSite("https://tntstats.000webhostapp.com/update.php", res, client);
                    }
                }
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String updateSite(final String url, final String json, final CloseableHttpClient client) throws IOException {
        HttpPost httppost = new HttpPost(url);

        StringEntity params = new StringEntity(json);
        httppost.addHeader("content-type", "application/x-www-form-urlencoded");
        httppost.setEntity(params);

        HttpResponse response = client.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                return Util.readInputStream(instream);
            }
        }
        return "";
    }
}
