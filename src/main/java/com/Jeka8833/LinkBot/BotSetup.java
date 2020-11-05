package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.command.Command;
import com.Jeka8833.LinkBot.command.Time;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class BotSetup extends TelegramLongPollingBot {

    private Map<String, Command> commandMap = new HashMap<>();

    final String name;
    final String token;

    public BotSetup(String name, String token) {
        this.name = name;
        this.token = token;
        commandMap.put("/time", new Time(this));
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String[] arg = update.getMessage().getText().split(" ", 1);
            if (commandMap.containsKey(arg[0].toLowerCase()))
                commandMap.get(arg[0].toLowerCase()).receiveListener(update, arg[1]);
            System.out.println(update.getMessage().getChatId() + " -> " + update.getMessage().getText());
           /* SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }*/
        }
    }
}
