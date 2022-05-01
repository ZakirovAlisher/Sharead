package com.example.site.controller;

import com.example.site.domain.ChatMessages;
import com.example.site.domain.Exchanges;
import com.example.site.domain.Offers;
import com.example.site.domain.Users;
import com.example.site.service.ExchangeService;
import com.example.site.service.MessageService;
import com.example.site.service.NotificationService;
import com.example.site.service.OfferService;
import com.example.site.service.UserService;
import com.example.site.service.WSService;
import com.example.site.util.Message;
import com.example.site.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private WSService service;

    @GetMapping(value = "/chat")
    public String chat(
            Model m, @RequestParam("opponent") String opponent,
            @RequestParam("exchange_id") Long exchangeId, @RequestParam("offer_id") Long offerId){
        Exchanges exchange = exchangeService.getExchange(exchangeId);
        Offers offer = offerService.getOffer(offerId);
        if(offer.isPicked()) {
            List<ChatMessages> messagesList = messageService.getChatMessagesByUserOneAndUserTwoAndExchange(
                    getUserData(), userService.getUserByEmail(opponent),
                    exchange);
            boolean confirmed = false;
            if (exchange.getFirstConfirm().equals(getUserData().getId()) || exchange.getSecondConfirm()
                                                                                    .equals(getUserData().getId())) {
                confirmed = true;
            }

            m.addAttribute("opponent", opponent);
            m.addAttribute("exchangeId", exchangeId);

            m.addAttribute("offerId", offerId);
            m.addAttribute("messages", messagesList);
            m.addAttribute("me", getUserData().getEmail());

            m.addAttribute("chatWith", userService.getUserByEmail(opponent));

            m.addAttribute("confirmed", confirmed);
            return "chat";
        }
        else  return "redirect:/profile";
    }
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessage getMessage(final Message message) throws InterruptedException {

        notificationService.sendGlobalNotification();
        return new ResponseMessage(
                "<b>"+ message.getOpponent() +"</b>" + "<p>" + message.getMessageContent() + "</p>");
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public ResponseMessage getPrivateMessage(final Message message,
                                             final Principal principal) throws InterruptedException {

        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setContent(message.getMessageContent());
        chatMessages.setExchange(exchangeService.getExchange(Long.parseLong(message.getExchangeId())));
        chatMessages.setUserOne(userService.getUserByEmail(message.getMe()));
        chatMessages.setUserTwo(userService.getUserByEmail(message.getOpponent()));
        messageService.addMessage(chatMessages);
        notificationService.sendPrivateNotification(message.getOpponent());
        service.notifyUser(message.getOpponent(), message.getMessageContent());
        return new ResponseMessage(
                " <div style=\"background-color: #b4de62; width: 35%\"   class=\"ml-auto p-3  m-2  rounded\"> <p >" + message.getMessageContent() + "</p> </div>"
        );
    }


    @PostMapping("/send-message")
    public void sendMessage(@RequestBody final Message message) {
        service.notifyFrontend(message.getMessageContent());
    }

    @PostMapping("/send-private-message/{id}")
    public void sendPrivateMessage(@PathVariable final String id,
                                   @RequestBody final Message message) {

        service.notifyUser(message.getOpponent(), message.getMessageContent());
    }

    private Users getUserData(){
        Authentication authontication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authontication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authontication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }
}
