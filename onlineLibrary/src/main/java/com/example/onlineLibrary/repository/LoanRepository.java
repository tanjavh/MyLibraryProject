package com.example.onlineLibrary.repository;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {


    boolean existsByUserAndReturnedFalse(User user);

    boolean existsByBookIdAndReturnedFalse(Long bookId);

    Optional<Loan> findByBookIdAndUserUsernameAndReturnedFalse(Long bookId, String username);


    @Query("SELECT l FROM Loan l JOIN FETCH l.user WHERE l.user.username = :username AND l.returned = false")
    List<Loan> findActiveLoansByUsername(@Param("username") String username);

    boolean existsByBookIdAndUserUsernameAndReturnedFalse(Long bookId, String username);

    int countByUser_UsernameAndReturnedFalse(String username);


}