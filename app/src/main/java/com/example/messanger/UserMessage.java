package com.example.messanger;

import java.io.Serializable;

class UserMessage implements Serializable {
    private String message;
    private String sender;
    private String receiver;
    private Long timeStamp;

    UserMessage(String message, String sender, String receiver, Long timeStamp) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timeStamp = timeStamp;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
