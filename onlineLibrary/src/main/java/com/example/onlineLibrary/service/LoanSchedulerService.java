
package com.example.onlineLibrary.service;


import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanSchedulerService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    // 1️⃣ Metoda koja blokira korisnike koji nisu vratili knjige više od 30 dana
    @Transactional
    public void blockOverdueUsers() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        List<User> usersToBlock = loanRepository.findAll()
                .stream()
                .filter(loan -> !loan.isReturned() && loan.getLoanDate().isBefore(thirtyDaysAgo))
                .map(loan -> loan.getUser())
                .distinct()
                .toList();

        for (User user : usersToBlock) {
            user.setBlocked(true); // blokira korisnika
            userRepository.save(user);
        }
    }

    // 2️⃣ Scheduler koji poziva ovu metodu svaki dan u ponoć
    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueLoans() {
        blockOverdueUsers();
    }
}
