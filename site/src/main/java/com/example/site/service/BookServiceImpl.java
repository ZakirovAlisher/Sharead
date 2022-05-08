package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Books> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Books addBook(Books book) {
        return bookRepository.save(book);
    }

    @Override
    public Books saveBook(Books book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Books> getAllBooksSearch(String str) {
        return bookRepository.getBooksSearch("%" + str + "%");
    }

    @Override
    public Books getBook(Long id) {
        return bookRepository.getOne(id);
    }

    @Override
    public void deleteBook(Books book) {
        bookRepository.delete(book);}
}
