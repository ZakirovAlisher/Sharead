package com.example.site.repository;

import com.example.site.domain.Exchanges;
import com.example.site.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ExchangeRepository extends JpaRepository<Exchanges, Long> {

    List<Exchanges> getExchangesByStatusOrderByDateDesc(String status);

    @Query("SELECT e FROM Exchanges e WHERE EXISTS (select o from Offers o where o.isPicked = true and o .user = ?1 ) AND e.status = 'freezed'")
    List<Exchanges> getExchangesByOffersAccepted(Users user); // где меня апрувнули

    @Query("SELECT e FROM Exchanges e WHERE e.status = 'freezed' and e.user = ?1")
    List<Exchanges> getExchangesByOffersAcceptedByMe(Users user);

}