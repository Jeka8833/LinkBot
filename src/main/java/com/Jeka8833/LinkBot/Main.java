package com.Jeka8833.LinkBot;

import com.Jeka8833.HelloSite.Site;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.TntCommunity.Server;
import com.Jeka8833.dataBase.DatabaseManager;
import com.Jeka8833.dataBase.LinkBotDB;
import com.google.gson.Gson;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static final Gson GSON = new Gson();

    public static void main(String[] args) throws TelegramApiException {
        DatabaseManager.initConnect(Util.getParam(args, "-database_url"));
        KPI.init();
        LinkBotDB.read();
        Server.main(args);
        //Site.main(args);
        var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotSetup(Util.getParam(args, "-name"), Util.getParam(args, "-token")));
    }
}
