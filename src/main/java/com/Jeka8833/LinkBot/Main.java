package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.TntCommunity.TntCommunity;
import com.Jeka8833.TntCommunity.WebServer;
import com.Jeka8833.dataBase.DatabaseManager;
import com.Jeka8833.dataBase.LinkBotDB;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static DatabaseManager db;

    public static void main(String[] args) throws TelegramApiException {
        db = new DatabaseManager(Util.getParam(args, "-db_url"), Util.getParam(args, "-db_user"),
                Util.getParam(args, "-db_pass"));
        db.connect();
        TntCommunity.main(args);
        WebServer.main(args);
        KPI.init();
        LinkBotDB.read();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotSetup(Util.getParam(args, "-name"), Util.getParam(args, "-token")));
        System.out.println("All user which enable notification:");
        for (User user : LinkBotDB.users) {
            if (user.notification == 0)
                continue;
            System.out.println(user.chatId + " - " + user.notification);
        }
    }
}
