package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.kpi.KPI;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        try {
            MySQL.connect(getParam(args, "-host"), getParam(args, "-user"), getParam(args, "-pass"), getParam(args, "-bdname"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MySQL.read();
        KPI.init();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotSetup(getParam(args, "-name"), getParam(args, "-token")));
    }

    private static String getParam(final String[] args, final String key) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equalsIgnoreCase(key))
                return args[i + 1];
        return System.getenv(key.substring(1).toUpperCase());
    }
}
