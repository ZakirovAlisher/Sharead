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
import org.springframework.security.access.prepost.PreAuthorize;
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
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private OfferService offerService;

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model m){
        m.addAttribute("currentUser", getUserData());
        m.addAttribute("myApproved", offerService.getMyApprovedOffers(getUserData()));
        m.addAttribute("iApproved", offerService.getIApprovedOffers(getUserData()));

        return "profile";
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
