package com.example.admin.controller;

import com.example.site.domain.Roles;
import com.example.site.domain.Users;
import com.example.site.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminUserController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/user_details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String userdetails(Model m, @PathVariable(name = "id") Long id){
        Users item = userService.getUser(id);
        List<Roles> roles = userService.getAllRoles();
        m.addAttribute("roles", roles);
        m.addAttribute("user", item);
        m.addAttribute("currentUser", getUserData());
        return "edit_user";
    }


    @PostMapping(value = "/assignrole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String assignrole(
            @RequestParam(name="user_id") Long iid,
            @RequestParam(name="role_id") Long cid,
            @RequestParam(name="del",defaultValue = "0") int del

                            )
    {

        if(del == 1){

            Roles cat = userService.getRole(cid);
            if (cat != null) {
                Users user = userService.getUser(iid);
                if (user != null) {
                    List<Roles> categories = user.getRoles();

                    categories.remove(cat);
                    userService.saveUser(user);
                    return "redirect:/user_details/" + iid +"#roles_table";
                }

            }

        }
        else {

            Roles cat = userService.getRole(cid);
            if (cat != null) {
                Users user = userService.getUser(iid);
                if (user != null) {
                    List<Roles> categories = user.getRoles();
                    if (categories == null) {
                        categories = new ArrayList<>();
                    }
                    categories.add(cat);
                    userService.saveUser(user);
                    return "redirect:/user_details/" + iid +"#roles_table";
                }

            }
        }

        return "redirect:/";
    }

    @PostMapping(value = "/adduser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String adduser(@RequestParam(name="name") String name,
                          @RequestParam(name="pass") String pass,

                          @RequestParam(name="email") String email

                         )



    {


        ArrayList<Roles> r = new ArrayList<Roles>();
        r.add(userService.getRole(1L));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userService.addUser(new Users(null, email, passwordEncoder.encode(pass), name, null, r));


        return "redirect:/admin";
    }



    @PostMapping(value = "/eduser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String saveUser(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
            @RequestParam(name="pass") String pass,

            @RequestParam(name="email") String email,
            @RequestParam(name="del", defaultValue = "0") int del

                          )
    {

        if(del == 1){
            userService.deleteUser(userService.getUser(id));

            return "redirect:/admin" ;
        }
        else{
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Users i = userService.getUser(id);
            i.setFullName(name);
            i.setEmail(email);


            i.setPassword(passwordEncoder.encode(pass));



            userService.saveUser(i);

            return "redirect:/user_details/" + id ;}
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
