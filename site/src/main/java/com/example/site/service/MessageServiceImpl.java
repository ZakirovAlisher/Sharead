package com.example.site.service;

import com.example.site.domain.ChatMessages;
import com.example.site.domain.Exchanges;
import com.example.site.domain.Offers;
import com.example.site.domain.Users;
import com.example.site.repository.MessageRepository;
import com.example.site.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;


    @Override
    public ChatMessages addMessage(ChatMessages offer) {
        return messageRepository.save(offer);
    }



    @Override
    public List<ChatMessages> getChatMessagesByUserOneAndUserTwoAndExchange(
            Users one,
            Users two,
            Exchanges exchange) {
        return messageRepository.findChatMessagesByUserOneAndUserTwoAndExchange(one, two, exchange);
    }


}
