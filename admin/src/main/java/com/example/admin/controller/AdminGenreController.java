package com.example.admin.controller;

import com.example.site.domain.Authors;
import com.example.site.domain.Books;
import com.example.site.domain.Genres;
import com.example.site.domain.Users;
import com.example.site.service.AuthorService;
import com.example.site.service.BookService;
import com.example.site.service.GenreService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminGenreController {


    @Autowired
    GenreService genreService;


    @Autowired
    UserService userService;



    @PostMapping(value = "/addGenre")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addBr(@RequestParam(name="name") String name)


    {

        genreService.addGenre(new Genres(null,name));



        return "redirect:/admin";
    }

    @PostMapping(value = "/editGenre")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String saveBr(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
            @RequestParam(name="del", defaultValue = "0") int del

                        )
    {

        if(del == 1){
            genreService.deleteGenre(genreService.getGenre(id));

            return "redirect:/admin";
        }
        else{

            Genres i = genreService.getGenre(id);
            i.setName(name);


            genreService.saveGenre(i);

            return "redirect:/admin";

        }
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
