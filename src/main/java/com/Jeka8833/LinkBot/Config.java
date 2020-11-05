package com.Jeka8833.LinkBot;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public Map<Integer, String> urls = new HashMap<>();

    private static final String url = "";
    private static final Gson gson = new Gson();

    public static Config config;

    public static void init() {
        try {
            config = gson.fromJson(Util.readSite(url), Config.class);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
