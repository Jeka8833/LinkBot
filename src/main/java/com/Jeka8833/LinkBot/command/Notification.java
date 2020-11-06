package com.Jeka8833.LinkBot.command;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Notification implements Command {
    private final TelegramLongPollingBot pollingBot;

    public Notification(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }


    @Override
    public void receiveListener(Update update, String text) {

    }
}
