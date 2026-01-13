package com.example.onlineLibrary.service;

import com.example.onlineLibrary.security.UserPrincipal;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security login
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: " + username));
        return new UserPrincipal(user); // UserPrincipal implementira UserDetails
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

}
