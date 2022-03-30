package com.example.sharead.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="email", length =255)
    private String email;
    @Column(name="password", length =255)
    private String password;
    @Column(name="fullname", length =255)
    private String fullName;

    @Column(name="user_avatar" )
    private String userAvatar;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Roles> roles;
}