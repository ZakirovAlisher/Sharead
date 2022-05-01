package com.example.site.util;

public class Message {
    private String messageContent;
    private String opponent;
    private String exchangeId;
    private String me;

    public String getMe() {
        return me;
    }

    public void setMe(final String me) {
        this.me = me;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(final String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(final String opponent) {
        this.opponent = opponent;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
