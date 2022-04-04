package com.example.site.service;

import com.example.site.domain.Authors;
import com.example.site.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService{
    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<Authors> getAllAuthors() { return authorRepository.findAll(); }

    @Override
    public Authors addAuthor(Authors author) { return authorRepository.save(author); }

    @Override
    public Authors getAuthor(Long id) { return authorRepository.getOne(id); }

    @Override
    public void deleteAuthor(Authors author) { authorRepository.delete(author); }

    @Override
    public Authors saveAuthor(final Authors author) {
        return authorRepository.save(author);
    }
}
