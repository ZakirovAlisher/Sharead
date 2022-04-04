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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminBookController {

    @Autowired
    UserService userService;

    @Autowired
    GenreService genreService;

    @Autowired
    AuthorService authorService;

    @Autowired
    BookService bookService;

    @GetMapping(value = "/book_details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String bookDetails(Model m, @PathVariable(name = "id") Long id){
        Books book = bookService.getBook(id);
        List<Genres> genres = genreService.getAllGenres();
        List<Authors> authors = authorService.getAllAuthors();

        m.addAttribute("genres", genres);
        m.addAttribute("authors", authors);
        m.addAttribute("book", book);

        m.addAttribute("currentUser", getUserData());

        return "edit_book";
    }

    @PostMapping(value = "/assign_genre")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String assGenre(
            @RequestParam(name= "book_id") Long bookId,
            @RequestParam(name= "genre_id") Long genreId,
            @RequestParam(name="del",defaultValue = "0") int del

                        )
    {

        Genres genre = genreService.getGenre(genreId);
        if (genre != null) {
            Books book = bookService.getBook(bookId);
            if (book != null) {
                List<Genres> genres = book.getGenres();
                if(del == 1) {
                    genres.remove(genre);

                } else {
                    if (genres == null) {
                        genres = new ArrayList<>();
                    }
                    genres.add(genre);
                }
                bookService.saveBook(book);
                return "redirect:/book_details/" + bookId;
            }
        }


        return "redirect:/";
    }



    @PostMapping(value = "/addBook")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addItem(@RequestParam(name="title") String name,
                          @RequestParam(name="author_id", defaultValue = "0") Long id
                         )



    {
        Authors br = authorService.getAuthor(id);

        if(br!=null){

            bookService.addBook(new Books(null, name, br, null));
        }


        return "redirect:/admin";
    }

    @PostMapping(value = "/editBook")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String saveTask(
            @RequestParam(name="id") Long id,
            @RequestParam(name="title") String name,
            @RequestParam(name="author_id") Long authorId,
            @RequestParam(name="del", defaultValue = "0") int del

                          )
    {

        if(del == 1){
            bookService.deleteBook(bookService.getBook(id));

            return "redirect:/admin";
        }
        else{
            Books i = bookService.getBook(id);
            i.setTitle(name);
            i.setAuthor(authorService.getAuthor(id));

            bookService.saveBook(i);

            return "redirect:/book_details/ "+ i.getId ();
        }}



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
