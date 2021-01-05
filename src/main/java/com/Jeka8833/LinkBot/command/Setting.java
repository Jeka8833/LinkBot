package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.User;
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
        if (!MySQL.users.isEmpty()) {
            if (!Util.isAdmin(update.getMessage().getChatId())) {
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ты не админ");
                return;
            }
        }
        final String[] args = text.split(" ");
        switch (args[0].toLowerCase()) {
            case "weekshift":
                try {
                    MySQL.shiftWeek = Integer.parseInt(args[1]);
                    MySQL.write(MySQL.Table.SETTING);
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "onnotification":
                try {
                    MySQL.onNotification = Integer.parseInt(args[1]);
                    MySQL.write(MySQL.Table.SETTING);
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "addlink":
                try {
                    MySQL.urls.put(Integer.parseInt(args[1]), args[2]);
                    MySQL.write(MySQL.Table.LINK);
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "reload":
                try {
                    KPI.init();
                    MySQL.reconnect();
                    MySQL.read();
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception throwables) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            default:
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Команды:" +
                        "\n- weekShift [int]" +
                        "\n- onnotification [int]" +
                        "\n- addLink [int(Key)] [String(link)]" +
                        "\n- reload");
        }
    }
}
