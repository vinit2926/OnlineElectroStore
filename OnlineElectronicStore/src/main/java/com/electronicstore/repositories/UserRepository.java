package com.electronicstore.repositories;

import com.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,String> {
    //custom finder methods
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email,String password);

    //search
    List<User> findByNameContaining(String keywords);
}
