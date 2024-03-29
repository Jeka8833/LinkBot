package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import com.Jeka8833.dataBase.LinkBotDB;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class ListCmd implements Command {

    private final TelegramLongPollingBot pollingBot;

    public ListCmd(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final User user = Util.getUser(update.getMessage().getChatId());
        if (user == null) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "",
                    "Ты кто? Напиши '/start', а уже потом '/list'");
            return;
        }

        int nowWeek = KPI.getWeek();
        StringBuilder sb = new StringBuilder();
        for (int week = 1; week <= 2; week++) {

            sb.append("_Неделя ").append(week);
            if (nowWeek == week - 1) sb.append("(Текущая)");
            sb.append("_\n");

            for (int day = 1; day <= 6; day++) {
                List<Lesson> dayLesson = KPI.getDayLessons(week - 1, day);
                if (dayLesson.isEmpty()) continue;

                sb.append(Util.getDayName(day)).append('\n');

                for (Lesson lesson : dayLesson) {
                    sb.append(lesson.lesson_number).append(") ").append(lesson.lesson_name).append(" `")
                            .append(lesson.lesson_type).append('`');

                    if (user.isSkipLesson(lesson.lesson_id)) {
                        sb.append(" [Skipping]");
                    }
                    if (text.equalsIgnoreCase("root")) {
                        sb.append(" -> ").append(lesson.lesson_id);
                    }
                    sb.append('\n');

                    if (lesson.online) {
                        String link = LinkBotDB.urls.getOrDefault(lesson.lesson_id, "-");
                        if (!(link.isBlank() || link.strip().equals("-"))) {
                            sb.append("    > ").append(link).append("\n");
                        }
                    } else {
                        if (!(lesson.lesson_class.isBlank() || lesson.lesson_class.strip().equals("-"))) {
                            sb.append("    > ").append("Аудитория: ").append(lesson.lesson_class).append("\n");
                        }
                    }
                }
                sb.append('\n');
            }
        }
        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", sb.toString());
    }
}
