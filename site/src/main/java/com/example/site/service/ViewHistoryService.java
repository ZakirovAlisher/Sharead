package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.domain.Users;
import com.example.site.domain.ViewHistory;
import com.example.site.util.BooksCounterDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ViewHistoryService {
    List<ViewHistory> getAllViewHistories();
    List<ViewHistory> getViewHistoriesByBook(Books book);
    List<ViewHistory> getViewHistoriesByUser(Users user);

    Set<ViewHistory> getViewHistoriesWithSameBooks(
            Users user,
            Set<Books> book);

    ViewHistory getViewHistoriesByUserAndBook(Users user, Books book);
    ViewHistory addViewHistory(ViewHistory viewHistory);
    ViewHistory saveViewHistory(ViewHistory viewHistory);

    List<Users> getUsersWithSameViews(
            Users userData,
            Set<Books> userPreferredBooks);

    List<BooksCounterDTO> getOthersPrefferedBooks(
            List<Users> usersWithSameViews,
            Set<Books> userPreferredBooks);
}
