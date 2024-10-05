package com.electronicstore.services.impl;

import com.electronicstore.entities.User;
import com.electronicstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    //part of spring security

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //we are passing username beacuse our email is username
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with given email not found..."));
        //we are returning user because it is child of UserDetails
        return user;
    }
}
