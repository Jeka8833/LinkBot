package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.TntCommunity.TntCommunity;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        TntCommunity.main(args);
        try {
            MySQL.connect(Util.getParam(args, "-host"), Util.getParam(args, "-user"),
                    Util.getParam(args, "-pass"), Util.getParam(args, "-bdname"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MySQL.read();
        KPI.init();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotSetup(Util.getParam(args, "-name"), Util.getParam(args, "-token")));
        System.out.println("All user which enable notification:");
        for (User user : MySQL.users) {
            if (user.notification == 0)
                continue;
            System.out.println(user.chatId + " - " + user.notification);
        }
    }
}
