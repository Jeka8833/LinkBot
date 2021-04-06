package com.Jeka8833.LinkBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Util {

    public static String readSite(final String url) {
        try (final InputStream inputStream = new URL(url).openStream();
             final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Fail connect or read site -> " + url);
    }

    public static void sendMessage(final TelegramLongPollingBot bot, final String chatId, final String text){
        final SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdown(true);
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAdmin(final long userId) {
        for (User user : MySQL.users) {
            if (user.chatId == userId && user.isAdmin == 1)
                return true;
        }
        return false;
    }

    public static String toTimeFormat(final int second) {
        int hours = second / 3600;
        int mins = second / 60 % 60;
        int secs = second % 60;
        return (hours < 10 ? "0" : "") + hours + ":" + (mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs;
    }
}
