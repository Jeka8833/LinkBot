package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

public class Next implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Next(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final User user = Util.getUser(update.getMessage().getChatId());
        if (user == null) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ты кто? Напиши '/start', а уже потом '/now'");
            return;
        }
        final List<Lesson> lessonList = KPI.getNextLesson().stream().filter(lesson -> !user.isSkipLesson(lesson.lesson_id)).collect(Collectors.toList());
        if (lessonList.isEmpty()) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Уже ничего не будет");
        } else {
            for (Lesson lesson : lessonList) {
                Util.sendMessage(pollingBot, user.chatId + "", "Скоро будет пара:" +
                        "\nПара: " + lesson.lesson_number + "(" + lesson.time_start + " - " + lesson.time_end + ")" +
                        "\nНазвание: " + lesson.lesson_name +
                        "\nТип: " + lesson.lesson_type + (lesson.online ? " Онлайн" : "") + (lesson.choice ? " Факультатив" : "") +
                        "\nПреподаватель: " + lesson.teacher_name +
                        (lesson.online ? "\nСсылка: " + MySQL.urls.getOrDefault(lesson.lesson_id, "-")
                                : "\nАудитория: " + lesson.lesson_class));
            }
        }
    }

}
