package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Setting implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Setting(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final String[] args = text.split(" ");
        for (int i = 0; i < args.length; i++)
            args[i] = args[i].toLowerCase();
        switch (args[0]) {
            case "weekshift":
                try {
                    MySQL.shiftWeek = Integer.parseInt(args[1]);
                    MySQL.write();
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "addlink":
                try {
                    MySQL.urls.put(Integer.parseInt(args[1]), args[2]);
                    MySQL.write();
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "reload":
                try {
                    KPI.init();
                    MySQL.reconnect();
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception throwables) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "help":
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Команды:" +
                        "\n- weekShift [int]" +
                        "\n- addLink [int(Key)] [String(link)]" +
                        "\n- reload");
                break;
            default:
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Тебе здесь не рады");
        }
    }
}
