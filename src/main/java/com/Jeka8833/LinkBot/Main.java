package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Objects;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        Config.init();
        KPI.init();

        System.out.println(KPI.getCurrentLesson(KPI.getWeek(2, 0)));
        for(Lesson lesson : Objects.requireNonNull(KPI.getDayLessons(KPI.getWeek(2, 0))))
            System.out.println(lesson);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotSetup(getParam(args, "-name"), getParam(args, "-token")));
    }

    private static String getParam(final String[] args, final String key) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equalsIgnoreCase(key))
                return args[i + 1];
        throw new NullPointerException("Key not found");
    }
}
