package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class ListCmd implements Command {

    private static final String[] dayName = {"*Понедельник:*", "*Вторник:*", "*Среда:*", "*Четверг:*", "*Пятница:*", "*Суббота:*"};

    private final TelegramLongPollingBot pollingBot;

    public ListCmd(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final int nowWeek = KPI.getWeek();
        StringBuilder sb = new StringBuilder();
        for (int week = 1; week <= 2; week++) {
            sb.append("_Неделя ").append(week);
            if (nowWeek == week - 1)
                sb.append("(Текущая)");
            sb.append("_\n");
            for (int day = 1; day <= 6; day++) {
                List<Lesson> dayLesson = KPI.getDayLessons  (week - 1, day);
                if (dayLesson.isEmpty())
                    continue;
                sb.append(dayName[day - 1]).append('\n');
                for (Lesson lesson : dayLesson) {
                    sb.append(lesson.lesson_number).append(") ").append(lesson.lesson_name).append(" `").append(lesson.lesson_type).append('`');
                    if (text.equalsIgnoreCase("root")) {
                        sb.append(" -> ").append(lesson.lesson_id);
                    }
                    sb.append('\n');
                    sb.append("    > ").append(MySQL.urls.getOrDefault(lesson.lesson_id, "-")).append("\n");
                }
                sb.append('\n');
            }
        }
        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", sb.toString());
    }
}
