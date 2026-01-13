package com.example.onlineLibrary.repository;

import com.example.onlineLibrary.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Dohvata korisnika po username-u (potrebno za Spring Security)
    Optional<User> findByUsername(String username);

    // Proverava da li username postoji (korisno pri registraciji)
    boolean existsByUsername(String username);

    // Proverava da li email postoji (ako postoji email polje)
    boolean existsByEmail(String email);
}
