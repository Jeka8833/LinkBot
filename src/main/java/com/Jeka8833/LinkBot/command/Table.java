package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class Table implements Command {

    private static final Map<String, Timer> timers = new HashMap<>();
    private static final String[] dayName = {"*Понедельник:*", "*Вторник:*", "*Среда:*", "*Четверг:*", "*Пятница:*", "*Суббота:*"};
    private final TelegramLongPollingBot pollingBot;

    public Table(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        send(update.getMessage().getChatId() + "");
    }

    public void send(final String chatId) {
        if (timers.containsKey(chatId))
            timers.get(chatId).cancel();

        try {
            final List<Lesson> lessons = KPI.getDayLessons(KPI.getWeek());
            if (lessons == null) {
                Util.sendMessage(pollingBot, chatId, "Сегодня пар нет");
                return;
            }
            final SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.enableMarkdown(true);
            message.setText(messageGenerate(lessons));
            final int messageIndex = pollingBot.execute(message).getMessageId();

            final long time = System.currentTimeMillis();
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (System.currentTimeMillis() - time > 2 * 60 * 60 * 1000 || isLessonsComplete(lessons))
                            throw new NullPointerException();
                        final EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setMessageId(messageIndex);
                        editMessageText.enableMarkdown(true);
                        editMessageText.setText(messageGenerate(lessons));
                        pollingBot.execute(editMessageText);
                    } catch (TelegramApiException | NullPointerException ignored) {

                    } catch (Exception e) {
                        timer.cancel();
                        timers.remove(chatId);
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);
            timers.put(chatId, timer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String messageGenerate(final List<Lesson> lessons) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Рассписание на ").append(dayName[KPI.getDay()]).append('\n');
        for (Lesson lesson : lessons) {
            sb.append(lesson.lesson_number).append(") ").append(lesson.lesson_name, 0, Math.min(45, lesson.lesson_name.length())).append(" `")
                    .append(lesson.lesson_type).append('`').append('\n');
            sb.append("-> Время: ");
            if (lesson.timeToEnd() < KPI.getTimeInSecond()) {
                sb.append("Пара уже прошла");
            } else {
                sb.append(lesson.timeToStart() > KPI.getTimeInSecond() ?
                        Util.toTimeFormat(lesson.timeToStart() - KPI.getTimeInSecond()) : "Now").append(" - ")
                        .append(Util.toTimeFormat(lesson.timeToEnd() - KPI.getTimeInSecond()));
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    /**
     * Return true if all lessons finished
     */
    private static boolean isLessonsComplete(final List<Lesson> lessons) {
        final int time = KPI.getTimeInSecond();
        for (Lesson lesson : lessons)
            if (lesson.timeToEnd() > time)
                return false;
        return true;
    }
}
