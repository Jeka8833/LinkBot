package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.dataBase.DatabaseManager;
import com.Jeka8833.dataBase.LinkBotDB;
import com.google.gson.Gson;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static final Gson gson = new Gson();

    public static void main(String[] args) throws TelegramApiException {
        DatabaseManager.initConnect(Util.getParam(args, "-db_url"), Util.getParam(args, "-db_user"),
                Util.getParam(args, "-db_pass"));
        KPI.init();
        LinkBotDB.read();
        //Server.main(args);
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
