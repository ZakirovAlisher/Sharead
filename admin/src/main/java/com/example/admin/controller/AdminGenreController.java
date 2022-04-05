package com.example.admin.controller;

import com.example.site.domain.Genres;
import com.example.site.service.GenreService;
import com.example.site.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminGenreController {

    @Autowired
    GenreService genreService;

    @Autowired
    UserService userService;

    @PostMapping(value = "/addGenre")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addGenre(@RequestParam(name = "name") String name) {
        genreService.addGenre(new Genres(null, name));
        return "redirect:/admin";
    }

    @PostMapping(value = "/editGenre")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String editGenre(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "del", defaultValue = "0") int del) {

        if (del == 1) {
            genreService.deleteGenre(genreService.getGenre(id));
            return "redirect:/admin";
        } else {

            Genres i = genreService.getGenre(id);
            i.setName(name);

            genreService.saveGenre(i);

            return "redirect:/admin";
        }
    }
}
