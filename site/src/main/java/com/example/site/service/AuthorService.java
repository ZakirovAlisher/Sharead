package com.example.site.service;

import com.example.site.domain.Authors;

import java.util.List;

public interface AuthorService {
    List<Authors> getAllAuthors();
    Authors addAuthor(Authors author);
    Authors getAuthor(Long id);
    void deleteAuthor(Authors author);

    Authors saveAuthor(Authors i);
}
