package com.example.sharead.service;

import com.example.sharead.domain.Roles;
import com.example.sharead.domain.Users;
import com.example.sharead.repository.RoleRepository;
import com.example.sharead.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users myUser = userRepository.findByEmail(s);
        if(myUser != null){
            User secUser = new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
            return secUser;
        }
        throw new UsernameNotFoundException("USER NOT FOUND");
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users addUser(Users b) {
        return userRepository.save(b);
    }

    @Override
    public Users getUser(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public Roles getRole(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public Users saveUser(Users item) {
        return userRepository.save(item);
    }

    @Override
    public List<Roles> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Roles addRole(Roles b) {
        return roleRepository.save(b);
    }

    @Override
    public Roles saveRole(Roles item) {
        return roleRepository.save(item);
    }

    @Override
    public void deleteUser(Users user) {
        userRepository.delete(user);
    }
}
