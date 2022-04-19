package com.example.site.service;

import com.example.site.domain.Books;
import com.example.site.domain.Exchanges;
import com.example.site.repository.BookRepository;
import com.example.site.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Override
    public List<Exchanges> getAllExchanges() {
        return exchangeRepository.findAll();
    }

    @Override
    public Exchanges addExchange(Exchanges book) {
        return exchangeRepository.save(book);
    }

    @Override
    public Exchanges saveExchange(Exchanges book) {
        return exchangeRepository.save(book);
    }

    @Override
    public Exchanges getExchange(Long id) {
        return exchangeRepository.getOne(id);
    }

    @Override
    public void deleteExchange(Exchanges book) {exchangeRepository.delete(book);}
}
