package com.example.admin.controller;

import com.example.site.domain.Authors;
import com.example.site.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminAuthorController {

    @Autowired
    AuthorService authorService;

    @PostMapping(value = "/addAuthor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addAuthor(@RequestParam(name = "name") String name) {
        authorService.addAuthor(new Authors(null, name));
        return "redirect:/admin";
    }

    @PostMapping(value = "/editAuthor")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String editAuthor(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "del", defaultValue = "0") int del) {

        if (del == 1) {
            authorService.deleteAuthor(authorService.getAuthor(id));
            return "redirect:/admin";
        } else {
            Authors i = authorService.getAuthor(id);
            i.setName(name);

            authorService.saveAuthor(i);

            return "redirect:/admin";
        }
    }
}
