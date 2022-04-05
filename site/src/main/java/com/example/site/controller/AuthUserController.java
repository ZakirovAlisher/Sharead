package com.example.site.controller;

import com.example.site.domain.Roles;
import com.example.site.domain.Users;
import com.example.site.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

@Controller

public class AuthUserController {

    private SecretKeySpec secretKey;
    private byte[] key;

    @Autowired
    private UserService userService;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    public void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(
            String strToEncrypt,
            String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
        }
        return null;
    }

    public String decrypt(
            String strToDecrypt,
            String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
        }
        return null;
    }

    @GetMapping(value = "/login")
    public String login(Model m) {
        m.addAttribute("currentUser", getUserData());
        return "login";
    }


    @GetMapping(value = "/register")
    public String register(Model m) {
        m.addAttribute("currentUser", getUserData());
        return "register";
    }

    @PostMapping(value = "/reg")
    public String registerPost(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "pass") String pass,
            @RequestParam(name = "pass2") String pass2,
            @RequestParam(name = "email") String email,
            RedirectAttributes redirAttrs) {

        if (pass.equals(pass2)) {
            ArrayList<Roles> r = new ArrayList<Roles>();
            r.add(userService.getRole(1L));
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            userService.addUser(new Users(null, email, passwordEncoder.encode(pass), name, null, r));

            redirAttrs.addFlashAttribute("success", "Successfully registred");

            return "redirect:/login";
        } else {
            redirAttrs.addFlashAttribute("error", "Registration error");
            return "redirect:/register?error";
        }
    }


    @PostMapping(value = "/edprofile")
    public String editProfile(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "name") String name,
            RedirectAttributes redirAttrs

                           ) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Users i = userService.getUser(id);
        i.setFullName(name);

        userService.saveUser(i);
        redirAttrs.addFlashAttribute("success1", "Profile successfully updated.");
        return "redirect:/profile";
    }

    @PostMapping(value = "/edpass")
    public String editPassword(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "old") String old,
            @RequestParam(name = "new") String new1,
            @RequestParam(name = "new2") String new2,
            RedirectAttributes redirAttrs) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(old, getUserData().getPassword())) {
            Users i = userService.getUser(id);
            if (new1.equals(new2)) {
                i.setPassword(passwordEncoder.encode(new1));
                userService.saveUser(i);
            } else {
                redirAttrs.addFlashAttribute("errorP", "Confirm password doesnt match.");
                return "redirect:/profile?differentpasswords";
            }
        } else {
            redirAttrs.addFlashAttribute("errorP2", " Old password doesnt match.");
            return "redirect:/profile?olddoesntmatch";
        }

        redirAttrs.addFlashAttribute("successP", "Password successfully updated.");
        return "redirect:/profile";
    }


    @PostMapping(value = "/uploadavatar")
    @PreAuthorize("isAuthenticated()")
    public String avatar(
            @RequestParam(name = "user_ava") MultipartFile file,
            RedirectAttributes redirAttrs) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                Users currentUser = getUserData();

                String picName = DigestUtils.sha1Hex("avatar_" + currentUser.getId() + "_!Picture");
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);
                currentUser.setUserAvatar(picName);
                userService.saveUser(currentUser);
                redirAttrs.addFlashAttribute("successA", "Successfully updated avatar.");
                return "redirect:/profile?success";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        redirAttrs.addFlashAttribute("errorA", "Error download avatar.");
        return "redirect:/profile";
    }


    @GetMapping(value = "/viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody
    byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultPicture;
        if (url != null) {
            pictureURL = viewPath + url + ".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return org.apache.commons.io.IOUtils.toByteArray(in);
    }


    private Users getUserData() {
        Authentication authontication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authontication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authontication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }

}





