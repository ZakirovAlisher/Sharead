package com.example.site.service;

import com.example.site.domain.Genres;

import java.util.List;

public interface GenreService {
    List<Genres> getAllGenres();
    Genres addGenre(Genres genre);
    Genres getGenre(Long id);
    void deleteGenre(Genres genre);

    Genres saveGenre(Genres i);
}
