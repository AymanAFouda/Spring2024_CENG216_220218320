package com.example.imsystem;

public class Message {

    String type;
    String name;
    String from;
    String to;
    String text;
    String[] activeUsers;

    public Message() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(String[] connectedUsers) {
        this.activeUsers = connectedUsers;
    }
}

