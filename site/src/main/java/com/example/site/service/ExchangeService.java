package com.example.site.service;

import com.example.site.domain.Exchanges;
import com.example.site.domain.Users;

import java.util.List;

public interface ExchangeService {
    List<Exchanges> getAllExchanges();

    Exchanges addExchange(Exchanges book);

    Exchanges saveExchange(Exchanges book);

    Exchanges getExchange(Long id);

    void deleteExchange(Exchanges book);

    List<Exchanges> getExchangesByStatus(String status);

    List<Exchanges> getMyApprovedExchanges(Users user);

    List<Exchanges> getIApprovedExchanges(Users user);
}
