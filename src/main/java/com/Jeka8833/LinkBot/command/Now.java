package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Now implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Now(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        Lesson lesson = KPI.getCurrentLesson(KPI.getWeek());
        if (lesson == null)
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Уже ничего не будет");
        else
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Сейчас будет или уже идёт пара:" +
                    "\nПара: " + lesson.lesson_number + "(" + lesson.time_start + " - " + lesson.time_end + ")" +
                    "\nИндекс: " + lesson.lesson_id +
                    "\nНазвание: " + lesson.lesson_name +
                    "\nТип: " + lesson.lesson_type +
                    "\nПреподаватель: " + lesson.teacher_name +
                    "\nСсылка: " + MySQL.urls.getOrDefault(lesson.lesson_id, "-"));
    }
}
