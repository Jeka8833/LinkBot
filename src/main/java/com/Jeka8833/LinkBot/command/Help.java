package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.Util;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Help implements Command{

    private final TelegramLongPollingBot pollingBot;

    public Help(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Хелпик:" +
                "\n/time - оставшееся время" +
                "\n/next - показывает следующую пару"+
                "\n/now - показывает текущую пару" +
                "\n/list - расписание со ссылками" +
                "\n/start - клавиатура" +
                "\n/notification - уведомление до начала пары" +
                "\n/table - живое время");
    }
}
