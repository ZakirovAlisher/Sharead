package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookService;

    @Override
    public List<Books> getAllBooks() {
        return bookService.findAll();
    }

    @Override
    public Books addBook(Books book) {
        return bookService.save(book);
    }

    @Override
    public Books saveBook(Books book) {
        return bookService.save(book);
    }


    @Override
    public Books getBook(Long id) {
        return bookService.getOne(id);
    }

    @Override
    public void deleteBook(Books book) {bookService.delete(book);}
}
