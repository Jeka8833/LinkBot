package com.Jeka8833.LinkBot.command;

public interface Command {

    void receiveListener(final String text);
    String getCommand();
}
