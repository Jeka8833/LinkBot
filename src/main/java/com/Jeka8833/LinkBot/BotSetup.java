package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.command.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class BotSetup extends TelegramLongPollingBot {

    private final Map<Long, Long> throttle = new HashMap<>();
    private final Map<String, Command> commandMap = new HashMap<>();

    final String name;
    final String token;

    public BotSetup(String name, String token) {
        this.name = name;
        this.token = token;
        commandMap.put("/time", new Time(this));
        commandMap.put("/setting", new Setting(this));
        commandMap.put("/now", new Now(this));
        commandMap.put("/next", new Next(this));
        commandMap.put("/list", new ListCmd(this));
        commandMap.put("/help", new Help(this));
        commandMap.put("/notification", new Notification(this));
        commandMap.put("/start", new Start(this));
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
            final long chatId = update.getMessage().getChatId();
            final String messageText = update.getMessage().getText();
            System.out.println(update.getMessage().getFrom().getFirstName() + " " + chatId + " -> " + messageText);
            if (throttle.containsKey(chatId)) {
                if (throttle.get(chatId) + 1000 > System.currentTimeMillis()) {
                    Util.sendMessage(this, chatId + "", "Не спамь скотиняка");
                    return;
                } else {
                    throttle.remove(chatId);
                }
            }
            throttle.put(chatId, System.currentTimeMillis());
            final String[] arg = messageText.split(" ", 2);
            if (commandMap.containsKey(arg[0].toLowerCase()))
                commandMap.get(arg[0].toLowerCase()).receiveListener(update, arg.length > 1 ? arg[1] : "");
            else
                Util.sendMessage(this, chatId + "", "Лопиталь запрещён, а комманда '/help' - всегда резрешина");
        }
    }
}
