package com.example.sharead.service;

import com.example.sharead.domain.Roles;
import com.example.sharead.domain.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {


    boolean saveUser(Users user);
    Users getUserByE(String id);


    List<Users> AdmGetAllUsers();
    Users AdmAddUser(Users item);
    Users AdmGetUser(Long id);
    Users AdmSaveUser(Users item);
    void AdmDeleteUser(Users item);

    List<Roles> getAllRoles();
    Roles getRole(Long id);


    Users AdmSaveUserInfo(Users item);
}