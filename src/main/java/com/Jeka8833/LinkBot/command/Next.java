package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Next implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Next(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        Lesson lesson = KPI.getNextLesson(KPI.getWeek());
        if (lesson == null)
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ничего уже не будет, спи спокойно");
        else
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Следующая пара:" +
                    "\nПара: " + lesson.lesson_number + "(" + lesson.time_start + " - " + lesson.time_end + ")" +
                    "\nИндекс: " + lesson.lesson_id +
                    "\nНазвание: " + lesson.lesson_name +
                    "\nТип: " + lesson.lesson_type +
                    "\nПреподаватель: " + lesson.teacher_name +
                    "\nСсылка: " + MySQL.urls.getOrDefault(lesson.lesson_id, "-"));
    }

}
