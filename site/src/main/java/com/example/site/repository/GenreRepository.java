package com.example.site.repository;

import com.example.site.domain.Genres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GenreRepository extends JpaRepository<Genres, Long> {
}
