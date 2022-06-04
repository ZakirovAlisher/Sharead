package com.example.site.repository;

import com.example.site.domain.Books;
import com.example.site.domain.Exchanges;
import com.example.site.domain.Users;
import com.example.site.domain.ViewHistory;
import com.example.site.util.BooksCounterDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {
    List<ViewHistory> getViewHistoriesByBook(Books book);
    List<ViewHistory> getViewHistoriesByUser(Users user);
    ViewHistory getViewHistoriesByUserAndBook(Users user, Books book);

    @Query("SELECT v FROM ViewHistory v WHERE v.book in ?2 and v.user != ?1")
    Set<ViewHistory> getViewHistoriesWithSameBooks(Users user, Set<Books> books);

    @Query("SELECT DISTINCT v.user FROM ViewHistory v WHERE v.book in ?2 and v.user != ?1 ORDER BY v.counter DESC")
    List<Users> getUsersWithSameViews(
            Users userData,
            Set<Books> userPreferredBooks);

    @Query(nativeQuery = true)
    List<BooksCounterDTO> getOthersPrefferedBooks(
            List<Long> usersWithSameViews,
            List<Long> userPreferredBooks);
}
