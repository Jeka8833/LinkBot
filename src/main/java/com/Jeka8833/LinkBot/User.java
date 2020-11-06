package com.Jeka8833.LinkBot;

public class User {

    public final Long chatId;
    public final byte isNotification;
    public final byte isAdmin;

    public User(Long chatId, byte isNotification, byte isAdmin) {
        this.chatId = chatId;
        this.isNotification = isNotification;
        this.isAdmin = isAdmin;
    }
}
