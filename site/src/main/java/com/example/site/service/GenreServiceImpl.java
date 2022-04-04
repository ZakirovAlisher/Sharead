package com.example.site.service;

import com.example.site.domain.Genres;
import com.example.site.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService{

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public List<Genres> getAllGenres() {return genreRepository.findAll();}

    @Override
    public Genres addGenre(Genres genre) {return genreRepository.save(genre);}

    @Override
    public Genres getGenre(Long id) {return genreRepository.getOne(id);}

    @Override
    public void deleteGenre(Genres genre) {genreRepository.delete(genre);}

    @Override
    public Genres saveGenre(final Genres i) {
        return genreRepository.save(i);
    }
}
