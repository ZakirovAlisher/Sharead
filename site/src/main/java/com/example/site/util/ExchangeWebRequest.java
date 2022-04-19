package com.example.site.util;

import com.example.site.domain.Books;
import com.example.site.domain.UserBooks;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@SessionScope
public class ExchangeWebRequest {

    List<Long> userBooks = new ArrayList<>();
    //Map<String, ArrayList<Long>> exchanges = new HashMap<>();
    List<Long> books = new ArrayList<>();

    public List<Long> getUserBooks() {
        return userBooks;
    }


    public void addUserBook(final Long userBook) {
        this.userBooks.add(userBook);
    }
    public void addBook(final Long book) {
        this.books.add(book);
    }

    public void setUserBooks(final List<Long> userBooks) {
        this.userBooks = userBooks;
    }

    public List<Long> getBooks() {
        return books;
    }


    public void setBooks(final List<Long> books) {
        this.books = books;
    }
}