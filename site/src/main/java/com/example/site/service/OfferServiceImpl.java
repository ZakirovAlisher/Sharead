package com.example.site.service;

import com.example.site.domain.Exchanges;
import com.example.site.domain.Offers;
import com.example.site.repository.ExchangeRepository;
import com.example.site.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Override
    public List<Offers> getAllOffers() {
        return offerRepository.findAll();
    }

    @Override
    public Offers addOffer(Offers offer) {
        return offerRepository.save(offer);
    }

    @Override
    public Offers saveOffer(Offers offer) {
        return offerRepository.save(offer);
    }

    @Override
    public Offers getOffer(Long id) {
        return offerRepository.getOne(id);
    }

    @Override
    public void deleteOffer(Offers offer) {offerRepository.delete(offer);}

    @Override
    public List<Offers> getByExchange(Exchanges exchange) { return offerRepository.findOffersByExchange(exchange);}

}
