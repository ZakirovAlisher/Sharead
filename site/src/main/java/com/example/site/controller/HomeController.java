package com.example.site.controller;

import com.example.site.domain.Users;
import com.example.site.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public String index(Model model){
//        List<Categories> cats = itemService.getAllCategories();
//        model.addAttribute("cats", cats);
//        List<Items> items = itemService.getAllItems();
//        model.addAttribute("items", items);
//        List<Brands> brands = itemService.getAllBrands();
//        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", getUserData());
        return "index";
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
