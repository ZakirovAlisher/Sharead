package com.example.site.repository;

import com.example.site.domain.Books;
import com.example.site.domain.Users;
import com.example.site.domain.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {
    List<ViewHistory> getViewHistoriesByBook(Books book);
    List<ViewHistory> getViewHistoriesByUser(Users user);
    ViewHistory getViewHistoriesByUserAndBook(Users user, Books book);
}
