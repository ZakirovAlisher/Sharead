package com.example.site.controller;

import com.example.site.domain.Books;
import com.example.site.domain.UserBooks;
import com.example.site.domain.Users;
import com.example.site.service.BookService;
import com.example.site.service.UserBookService;
import com.example.site.service.UserService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class LibraryController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserBookService userBookService;

    @Value("${file.userBook.uploadPath}")
    private String uploadPath;

    @Value("${file.userBook.viewPath}")
    private String viewPath;

    @GetMapping(value = "/library")
    public String index(Model model) {
        List<UserBooks> userBooks = userBookService.getAllBooksByUser(getUserData());
        model.addAttribute("userBooks", userBooks);
        model.addAttribute("currentUser", getUserData());

        return "library";
    }
    
    @PostMapping(value = "/addBookToLibrary")
    @PreAuthorize("isAuthenticated()")
    public String addBookToLibrary(
            @RequestParam(name = "cover") MultipartFile file,
            RedirectAttributes redirAttrs) {

        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                Users currentUser = getUserData();

                byte[] bytes = file.getBytes();
                String imageString = Base64.getEncoder().encodeToString(bytes);


                String userBookText = this.resolveText(imageString);

                List<Books> books = this.bookService.getAllBooks();

                Books potentialBook = resolvePotentialBook(userBookText, books);

                String picName = DigestUtils.sha1Hex("userBook_" + potentialBook.getTitle() + currentUser.getFullName() + userBookText +  "_!Picture");

                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                UserBooks userBook = new UserBooks();
                userBook.setBook(potentialBook);
                userBook.setUser(currentUser);
                userBook.setCover(picName);
                this.userBookService.addBook(userBook);

                return "redirect:/library?success";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        redirAttrs.addFlashAttribute("errorA", "Error download avatar.");
        return "redirect:/profile";
    }

    private Books resolvePotentialBook(
            final String userBookText,
            final List<Books> books) {
        Books potentialBook = null;
        int minLevenstain = Integer.MAX_VALUE;
        for (Books book: books) {
            String bookText = book.getTitle() + " " + book.getAuthor().getName();
            int currentLevenstain = this.levenstain(userBookText.toLowerCase(), bookText.toLowerCase());
            if(currentLevenstain < minLevenstain){
                minLevenstain = currentLevenstain;
                potentialBook = book;
            }

        }

        return potentialBook;
    }

    private String resolveText(final String imageString) throws IOException, ParseException {
        URL serverUrl = new URL("https://vision.googleapis.com/v1/images:annotate?" + "key=AIzaSyB9xz97wzq6cEju0RMs8Yqp2C8vUjQKuP4"); //TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?"
        URLConnection urlConnection = serverUrl.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;

        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        httpConnection.setDoOutput(true);

        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()));

        httpRequestBodyWriter.write(String.format("         {\n" 
                                                          + "  \"requests\": [\n" 
                                                          + "    {\n" 
                                                          + "      \"features\": [\n" 
                                                          + "        {\n" 
                                                          + "          \"type\": \"DOCUMENT_TEXT_DETECTION\"\n"
                                                          + "        }\n" 
                                                          + "      ],\n" 
                                                          + "      \"image\": {\n" 
                                                          + "        \"content\": \"%s\"\n" 
                                                          + "      },\n" 
                                                          + "    }\n" 
                                                          + "  ]\n" 
                                                          + "}", imageString));
        httpRequestBodyWriter.close();
       
        Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
            resp += line;
            System.out.println(line);  //  alternatively, print the line of response
        }
        httpResponseScanner.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(resp);
        JSONArray k = (JSONArray) json.get("responses");
        JSONObject k2 = (JSONObject) k.get(0);
        JSONObject k3 = (JSONObject)k2.get("fullTextAnnotation");
        String resultText = (String) k3.get("text");
        resultText = resultText.replace("\n", " ");

        return resultText;
    }

    public  int levenstain( String str1,  String str2) {
        int[] Di_1 = new int[str2.length() + 1];
        int[] Di = new int[str2.length() + 1];

        for (int j = 0; j <= str2.length(); j++) {
            Di[j] = j; // (i == 0)
        }

        for (int i = 1; i <= str1.length(); i++) {
            System.arraycopy(Di, 0, Di_1, 0, Di_1.length);

            Di[0] = i; // (j == 0)
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) != str2.charAt(j - 1)) ? 1 : 0;
                Di[j] = min(
                        Di_1[j] + 1,
                        Di[j - 1] + 1,
                        Di_1[j - 1] + cost
                           );
            }
        }

        return Di[Di.length - 1];
    }

    private static int min(int n1, int n2, int n3) {
        return Math.min(Math.min(n1, n2), n3);
    }

    @PostMapping(value = "/removeBookFromLibrary")
    public String removeBookFromLibrary(
            @RequestParam(name = "book_id") Long id) {
        UserBooks book = userBookService.getBook(id);
        book.setRemoved(true);
        userBookService.saveBook(book);

        return "redirect:/library?success";

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

    @GetMapping(value = "/viewuserbook/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody
    byte[] viewBookPhoto(@PathVariable(name = "url") String url) throws IOException {

        String pictureURL = "";
        if (url != null) {
            pictureURL = viewPath + url + ".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return org.apache.commons.io.IOUtils.toByteArray(in);
    }

}
