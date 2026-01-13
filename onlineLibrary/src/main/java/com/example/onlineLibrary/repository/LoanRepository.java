package com.example.onlineLibrary.repository;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Sve pozajmice određenog korisnika
    List<Loan> findByUser(User user);

    // Provera da li korisnik trenutno ima aktivnu pozajmicu za određenu knjigu
    Optional<Loan> findByUserAndBookIdAndReturnedFalse(User user, Long bookId);
}
