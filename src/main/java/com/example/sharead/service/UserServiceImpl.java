package com.example.sharead.service;

import com.example.sharead.domain.Roles;
import com.example.sharead.domain.Users;
import com.example.sharead.repository.RolesRepository;
import com.example.sharead.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(s);
        if(user!=null){
            return user;
        }else{
            throw new UsernameNotFoundException("USER NOT FOUND");
        }

    }
    @Autowired
    private PasswordEncoder passwordEncoder;
    public boolean saveUser(Users user) {
        Users userFromDB = userRepository.findByEmail(user.getUsername());

        if (userFromDB != null) {
            return false;
        }
        List<Roles> roles = new ArrayList<> ();
        roles.add(new Roles(2L, "ROLE_USER"));
        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public Users getUserByE(String id) {
        return userRepository.findByEmail (id);
    }

    @Override
    public List<Users> AdmGetAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users AdmAddUser(Users item) {
        Users userFromDB = userRepository.findByEmail(item.getUsername());

        if (userFromDB != null) {
            return null;
        }
        List<Roles> roles = new ArrayList<> ();
        roles.add(new Roles(2L, "ROLE_USER"));
        item.setRoles(roles);

        item.setPassword(passwordEncoder.encode(item.getPassword()));
        return userRepository.save(item);

    }

    @Override
    public Users AdmGetUser(Long id) {
        Optional<Users> opt = userRepository.findById(id);
        return opt.isPresent()?opt.get():null;
    }

    @Override
    public Users AdmSaveUser(Users item) {
        item.setPassword(passwordEncoder.encode(item.getPassword()));
        return userRepository.save(item);
    }

    @Override
    public void AdmDeleteUser(Users item) {
        userRepository.delete(item);
    }
    @Override
    public Users AdmSaveUserInfo(Users item) {
        return userRepository.save(item);
    }
    @Override
    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public Roles getRole(Long id) {
        Optional<Roles> opt = rolesRepository.findById(id);
        return opt.isPresent()?opt.get():null;
    }


}