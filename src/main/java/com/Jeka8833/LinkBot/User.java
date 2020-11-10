package com.Jeka8833.LinkBot;

public class User {

    public final Long chatId;
    public byte notification;
    public final byte isAdmin;

    public User(Long chatId, byte isNotification, byte isAdmin) {
        this.chatId = chatId;
        this.notification = isNotification;
        this.isAdmin = isAdmin;
    }
}
