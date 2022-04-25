package com.example.site.service;

import com.example.site.domain.Exchanges;
import com.example.site.domain.Offers;
import com.example.site.domain.Users;

import java.util.List;

public interface OfferService {
    List<Offers> getAllOffers();

    Offers addOffer(Offers offer);

    Offers saveOffer(Offers offer);

    Offers getOffer(Long id);

    void deleteOffer(Offers offer);

    List<Offers> getByExchange(Exchanges exchange);

    List<Offers> getMyApprovedOffers(Users user);

    List<Offers> getIApprovedOffers(Users user);
}
