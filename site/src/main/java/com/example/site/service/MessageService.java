package com.example.site.service;

import com.example.site.domain.ChatMessages;
import com.example.site.domain.Exchanges;
import com.example.site.domain.Users;

import java.util.List;

public interface MessageService {
    ChatMessages addMessage(ChatMessages offer);

    List<ChatMessages> getChatMessagesByUserOneAndUserTwoAndExchange(
            Users one,
            Users two,
            Exchanges exchange);
}
