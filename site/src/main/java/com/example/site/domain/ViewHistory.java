package com.example.site.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "view_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Books book;

    @Column(name = "counter")
    private int counter;
}
