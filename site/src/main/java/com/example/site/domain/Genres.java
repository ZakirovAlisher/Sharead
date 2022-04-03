package com.example.site.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_genres")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Books> books;
}
