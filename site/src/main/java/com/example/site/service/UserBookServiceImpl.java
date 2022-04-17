package com.example.site.service;

import com.example.site.domain.UserBooks;
import com.example.site.domain.Users;
import com.example.site.repository.UserBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBookServiceImpl implements UserBookService {

    @Autowired
    private UserBookRepository userBookRepository;

    @Override
    public List<UserBooks> getAllBooks() {
        return userBookRepository.findAll();
    }

    @Override
    public List<UserBooks> getAllBooksByUser(Users user) {
        return userBookRepository.findUserBooksByUser(user);
    }

    @Override
    public UserBooks addBook(UserBooks book) {
        return userBookRepository.save(book);
    }

    @Override
    public UserBooks saveBook(UserBooks book) {
        return userBookRepository.save(book);
    }

    @Override
    public UserBooks getBook(Long id) {
        return userBookRepository.getOne(id);
    }

    @Override
    public void deleteBook(UserBooks book) {userBookRepository.delete(book);}
}
