package com.example.site.repository;

import com.example.site.domain.Books;
import com.example.site.domain.UserBooks;
import com.example.site.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserBookRepository extends JpaRepository<UserBooks, Long> {

    List<UserBooks> findUserBooksByUserAndAndRemoved(Users user, boolean removed);

    @Query("select b from UserBooks b where (b.book.author.name like ?1 or b.book.title like ?1) and b.user = ?2 and b.removed = 'false'")
    List<UserBooks> getAllUserBooksSearch(String searchUserStr, Users userData);
}