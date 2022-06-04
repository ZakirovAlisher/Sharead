package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.domain.Users;
import com.example.site.domain.ViewHistory;
import com.example.site.repository.ViewHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ViewHistoryImpl implements ViewHistoryService{

    @Autowired
    private ViewHistoryRepository viewHistoryRepository;

    @Override
    public List<ViewHistory> getAllViewHistories() {
        return viewHistoryRepository.findAll();
    }

    @Override
    public List<ViewHistory> getViewHistoriesByBook(Books book) {
        return viewHistoryRepository.getViewHistoriesByBook(book);
    }

    @Override
    public List<ViewHistory> getViewHistoriesByUser(Users user) {
        return viewHistoryRepository.getViewHistoriesByUser(user);
    }

    @Override
    public ViewHistory getViewHistoriesByUserAndBook(Users user, Books book) {
        return viewHistoryRepository.getViewHistoriesByUserAndBook(user, book);
    }

    @Override
    public ViewHistory addViewHistory(ViewHistory viewHistory) {
        return viewHistoryRepository.save(viewHistory);
    }

    @Override
    public ViewHistory saveViewHistory(ViewHistory viewHistory) {
        return viewHistoryRepository.save(viewHistory);
    }
}
