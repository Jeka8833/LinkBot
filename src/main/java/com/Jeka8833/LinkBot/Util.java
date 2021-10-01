package com.Jeka8833.LinkBot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Util {

    private static final String[] dayName = {"*Воскресенье:*", "*Понедельник:*", "*Вторник:*", "*Среда:*", "*Четверг:*", "*Пятница:*", "*Суббота:*"};

    public static String readInputStream(final @NotNull InputStream inputStream) {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Fail read InputStream");
    }

    public static void sendMessage(final @NotNull TelegramLongPollingBot bot, final String chatId, final String text) {
        final SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdown(true);
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("User error: " + chatId);
            e.printStackTrace();
        }
    }

    public static boolean isAdmin(final long userId) {
        for (User user : MySQL.users)
            if (user.chatId == userId && user.isAdmin == 1)
                return false;
        return true;
    }

    @Contract(pure = true)
    public static @Nullable User getUser(final long userId) {
        for (User user : MySQL.users)
            if (user.chatId == userId)
                return user;
        return null;
    }

    @Contract(pure = true)
    public static @NotNull String toTimeFormat(final int second) {
        int hours = second / 3600;
        int mins = second / 60 % 60;
        int secs = second % 60;
        return (hours < 10 ? "0" : "") + hours + ":" + (mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs;
    }


    public static String getParam(final String @NotNull [] args, final String key) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equalsIgnoreCase(key))
                return args[i + 1];
        return System.getenv(key.substring(1).toUpperCase());
    }

    public static int parseTime(final @NotNull String time) {
        final String[] arg = time.split(":");
        return Integer.parseInt(arg[0]) * 60 * 60 + Integer.parseInt(arg[1]) * 60 + Integer.parseInt(arg[2]);
    }

    public static byte @NotNull [] hexStringToByteArray(String s) {
        if (s == null)
            return new byte[0];
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getDayName(final int day) {
        return dayName[day];
    }
}
