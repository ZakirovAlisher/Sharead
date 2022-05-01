package com.example.site.domain;

import com.example.site.util.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "exchanges")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exchanges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private Date date;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    private String status = ExchangeStatus.OPENED;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @Column(name = "first_confirm")
    private Long firstConfirm = 0L;

    @Column(name = "second_confirm")
    private Long secondConfirm = 0L;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<UserBooks> userBooks;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Books> books;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Offers> offers;

}