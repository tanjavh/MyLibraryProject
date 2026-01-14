package com.example.LibraryMicroservice.repository;

import com.example.LibraryMicroservice.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUsername(String username);

    Optional<Loan> findByUsernameAndBookIdAndReturnedFalse(String username, Long bookId);
}
