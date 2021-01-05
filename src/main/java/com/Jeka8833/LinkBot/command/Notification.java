package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Notification implements Command {
    private final TelegramLongPollingBot pollingBot;

    public Notification(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }


    @Override
    public void receiveListener(Update update, String text) {
        try {
            if (!text.isEmpty()) {
                for (User user : MySQL.users) {
                    if (user.chatId.equals(update.getMessage().getChatId())) {
                        if (text.equalsIgnoreCase("0") || text.equalsIgnoreCase("off")) {
                            user.notification = 0;
                        } else {
                            user.notification = Byte.parseByte(text);
                        }
                        MySQL.write(MySQL.Table.NOTIFICATION);
                        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                        return;
                    }
                }
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ты кто? Напиши '/start', а уже потом '/notification'");
                return;
            }
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Пример использования команды:\n" +
                    "/notification 45 -> уведомить за 45 минут до начала пары\n" +
                    "/notification 0 -> выключить уведомление\n" +
                    "/notification off -> выключить уведомление");
        } catch (Exception ex) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Пример использования команды:\n" +
                    "/notification 45 -> увидомить за 45 минут до начала пары\n" +
                    "/notification 0 -> выключить уведомление\n" +
                    "/notification off -> выключить уведомление");
        }

    }
}
