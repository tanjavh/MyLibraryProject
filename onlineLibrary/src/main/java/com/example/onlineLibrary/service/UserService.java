package com.example.onlineLibrary.service;

import com.example.onlineLibrary.model.dto.UserViewDto;
import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.RoleRepository;
import com.example.onlineLibrary.security.UserPrincipal;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoanRepository loanRepository;



    // Spring Security login
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: " + username));
        return new UserPrincipal(user); // UserPrincipal implementira UserDetails
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    // Dodatne metode
    public User save(User user) {
        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser(); // vrati originalnog User entiteta
        }
        return null;
    }

    public java.util.Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
    public void delete(User user) {
        userRepository.delete(user);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User register(User user) {
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("ROLE USER ne postoji"));

        user.setRoles(Set.of(userRole));
        user.setActive(true);
        user.setBlocked(false);

        return userRepository.save(user);
    }
    public void deleteUser(Long id) {
        User user = getUserById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Provera da li korisnik ima aktivne pozajmice
        boolean hasActiveLoans = loanRepository.existsByUserAndReturnedFalse(user);
        if (hasActiveLoans) {
            throw new IllegalStateException("Korisnik ima aktivne pozajmice i ne može biti obrisan");
        }

        userRepository.delete(user);
    }
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Korisnik ne postoji"));
        user.setBlocked(true);
        userRepository.save(user);
    }
    public List<UserViewDto> getAllUsersForView() {
        return userRepository.findAll().stream()
                .map(user -> {

                    String roleLabel = user.getRoles().stream()
                            .anyMatch(r -> r.getName() == RoleName.ADMIN)
                            ? "admin"
                            : "user";

                    return UserViewDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .role(roleLabel)  // ovo ide u tabelu
                            .active(user.isActive())
                            .blocked(user.isBlocked())
                            .hasActiveLoans(loanRepository.existsByUserAndReturnedFalse(user))
                            .build();
                })
                .toList();
    }



    @Transactional
    public void updateUserUsername(String oldUsername, String newUsername) {
        User user = userRepository.findByUsername(oldUsername)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        // Optional: provera da username nije zauzet
        if (!user.getUsername().equals(newUsername) &&
                userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Username je već zauzet!");
        }

        user.setUsername(newUsername);

        userRepository.save(user);
    }
}