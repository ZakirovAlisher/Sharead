package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.domain.Users;
import com.example.site.domain.ViewHistory;

import java.util.List;

public interface ViewHistoryService {
    List<ViewHistory> getAllViewHistories();
    List<ViewHistory> getViewHistoriesByBook(Books book);
    List<ViewHistory> getViewHistoriesByUser(Users user);
    ViewHistory getViewHistoriesByUserAndBook(Users user, Books book);
    ViewHistory addViewHistory(ViewHistory viewHistory);
    ViewHistory saveViewHistory(ViewHistory viewHistory);
}
