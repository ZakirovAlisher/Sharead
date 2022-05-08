package com.example.site.repository;

import com.example.site.domain.Books;
import com.example.site.domain.Offers;
import com.example.site.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface BookRepository extends JpaRepository<Books, Long> {
    @Query("select b from Books b where b.author.name like ?1  or b.title like ?1 ")
    List<Books> getBooksSearch(String str);
}