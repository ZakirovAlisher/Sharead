package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.domain.Users;
import com.example.site.domain.ViewHistory;
import com.example.site.repository.ViewHistoryRepository;
import com.example.site.util.BooksCounterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<ViewHistory> getViewHistoriesWithSameBooks(
            Users user,
            Set<Books> books) {
        return viewHistoryRepository.getViewHistoriesWithSameBooks(user, books);
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

    @Override
    public List<Users> getUsersWithSameViews(
            final Users userData,
            final Set<Books> userPreferredBooks) {
        return  viewHistoryRepository.getUsersWithSameViews(userData, userPreferredBooks);
    }

    @Override
    public List<BooksCounterDTO> getOthersPrefferedBooks(
            final List<Users> usersWithSameViews,
            final Set<Books> userPreferredBooks) {
        final List<Long> userIds = usersWithSameViews.stream().map(Users::getId).collect(
                Collectors.toList());
        final List<Long> bookIds = userPreferredBooks.stream().map(Books::getId).collect(
                Collectors.toList());

        return viewHistoryRepository.getOthersPrefferedBooks(userIds, bookIds);
    }
}
