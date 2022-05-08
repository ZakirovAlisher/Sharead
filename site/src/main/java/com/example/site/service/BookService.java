package com.example.site.service;

import com.example.site.domain.Books;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BookService {
    List<Books> getAllBooks();
    Books addBook(Books book);
    Books getBook(Long id);
    void deleteBook(Books book);
    Books saveBook(Books book);

    List<Books> getAllBooksSearch(String str);
}
