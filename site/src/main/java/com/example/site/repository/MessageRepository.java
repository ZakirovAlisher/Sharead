package com.example.site.repository;

import com.example.site.domain.ChatMessages;
import com.example.site.domain.Exchanges;
import com.example.site.domain.Offers;
import com.example.site.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MessageRepository extends JpaRepository<ChatMessages, Long> {

    @Query("select o from ChatMessages o where ((o.userOne = ?1 and o.userTwo = ?2) or (o.userOne = ?2 and o.userTwo = ?1)) and o.exchange = ?3")
    List<ChatMessages> findChatMessagesByUserOneAndUserTwoAndExchange(Users userOne, Users userTwo, Exchanges exchange);

}