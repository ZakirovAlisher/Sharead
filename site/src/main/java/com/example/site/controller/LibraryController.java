package com.example.site.controller;

import com.example.site.domain.Users;
import com.example.site.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Controller
public class LibraryController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/library")
    public String index(Model model){
//        List<Categories> cats = itemService.getAllCategories();
//        model.addAttribute("cats", cats);
//        List<Items> items = itemService.getAllItems();
//        model.addAttribute("items", items);
//        List<Brands> brands = itemService.getAllBrands();
//        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", getUserData());
        return "library";
    }

    @PostMapping(value = "/addBookToLibrary")
    @PreAuthorize("isAuthenticated()")
    public String addBookToLibrary(
            @RequestParam(name="cover") MultipartFile file,
            RedirectAttributes redirAttrs)
    {
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

            try {
                Users currentUser = getUserData();

                //создать сущность юзер бук ис охранить туда обложку и айди замапленной книги



                byte[] bytes = file.getBytes();
                String encodedString = Base64.getEncoder().encodeToString(bytes);

//
//                String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_!Picture");
//                byte[] bytes = file.getBytes();
//                Path path = Paths.get(uploadPath + picName+".jpg");
//                Files.write(path, bytes);
//                currentUser.setUserAvatar(picName);
//                userService.saveUser(currentUser);
//                redirAttrs.addFlashAttribute("successA", "Successfully updated avatar.");


                return "redirect:/library?success" ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        redirAttrs.addFlashAttribute("errorA", "Error download avatar.");
        return "redirect:/profile" ;
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
