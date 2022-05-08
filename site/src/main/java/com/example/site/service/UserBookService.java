package com.example.site.service;

import com.example.site.domain.UserBooks;
import com.example.site.domain.Users;

import java.util.List;

public interface UserBookService {
    List<UserBooks> getAllBooks();
    List<UserBooks> getAllBooksByUser(Users user);
    UserBooks addBook(UserBooks book);
    UserBooks getBook(Long id);
    void deleteBook(UserBooks book);
    UserBooks saveBook(UserBooks book);

    List<UserBooks> getAllUserBooksSearch(
            String searchUserStr,
            final Users userData);
}
