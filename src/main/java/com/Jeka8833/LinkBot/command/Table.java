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

    private static Map<String, Timer> timers = new HashMap<>();

    private final TelegramLongPollingBot pollingBot;

    public Table(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final String chatId = update.getMessage().getChatId() + "";
        if (timers.containsKey(chatId))
            timers.get(chatId).cancel();

        try {
            final List<Lesson> lessons = KPI.getDayLessons(KPI.getWeek());
            if (lessons == null) {
                Util.sendMessage(pollingBot, chatId, "Сегодня пар нет");
                return;
            }
            final String outText = messageGenerate(lessons);
            if (outText == null) {
                Util.sendMessage(pollingBot, chatId, "Всё закончилось");
                return;
            }
            final SendMessage message = new SendMessage();
            message.setChatId(chatId);
            //message.enableMarkdown(true);
            message.setText(outText);
            final int messageIndex = pollingBot.execute(message).getMessageId();

            final long time = System.currentTimeMillis();
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (System.currentTimeMillis() - time > 60 * 60 * 1000)
                            throw new NullPointerException();
                        final String text = messageGenerate(lessons);
                        final EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setMessageId(messageIndex);
                        editMessageText.setText(text == null ? "Всё закончилось" : text);
                        pollingBot.execute(editMessageText);
                        if (text == null)
                            throw new NullPointerException();
                    } catch (TelegramApiException | NullPointerException telegramApiException) {

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
        StringBuilder sb = new StringBuilder();
        for (Lesson lesson : lessons) {
            if (lesson.timeToEnd() < KPI.getTimeInSecond()) continue;
            sb.append(lesson.lesson_number).append(" пара: ").append(lesson.timeToStart() > KPI.getTimeInSecond()
                    ? Util.toTimeFormat(lesson.timeToStart() - KPI.getTimeInSecond()) : "Now").append(" - ")
                    .append(Util.toTimeFormat(lesson.timeToEnd() - KPI.getTimeInSecond())).append('\n');
        }
        return sb.toString().isEmpty() ? null : sb.toString();
    }
}
