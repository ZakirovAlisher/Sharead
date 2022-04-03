package com.example.site.repository;

import com.example.site.domain.Authors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AuthorRepository extends JpaRepository<Authors, Long> {
}
