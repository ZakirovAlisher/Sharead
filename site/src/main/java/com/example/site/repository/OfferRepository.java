package com.example.site.repository;

import com.example.site.domain.Exchanges;
import com.example.site.domain.Offers;
import com.example.site.domain.UserBooks;
import com.example.site.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OfferRepository extends JpaRepository<Offers, Long> {
    List<Offers> findOffersByExchange(Exchanges exchange);

    @Query("select o from Offers o where o.isPicked = true and o .user = ?1")
    List<Offers> getExchangesByOffersAccepted(Users user); // где меня апрувнули

    @Query("select o from Offers o where o.isPicked = true and o .exchange.user = ?1")
    List<Offers> getExchangesByOffersAcceptedIApproved(Users user); //оффер который я пикнул
}