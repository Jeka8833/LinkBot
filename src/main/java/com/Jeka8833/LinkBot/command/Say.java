package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Say implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Say(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        if (!MySQL.users.isEmpty()) {
            if (!Util.isAdmin(update.getMessage().getChatId())) {
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ты не админ");
                return;
            }
        }
        for (User user : MySQL.users) {
            Util.sendMessage(pollingBot, String.valueOf(user.chatId), text);
        }
    }
}
