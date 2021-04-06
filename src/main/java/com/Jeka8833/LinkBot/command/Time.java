package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Time implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Time(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(final Update update, final String text) {
        final Lesson lesson = KPI.getCurrentLesson(KPI.getWeek());
        if (lesson == null) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Всё закончилось");
        } else if (lesson.timeToStart() > KPI.getTimeInSecond()) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "До начала осталось: " + Util.toTimeFormat(lesson.timeToStart() - KPI.getTimeInSecond()));
        } else if (lesson.timeToEnd() > KPI.getTimeInSecond()) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "До конца осталось: " + Util.toTimeFormat(lesson.timeToEnd() - KPI.getTimeInSecond()));
        }
    }
}
