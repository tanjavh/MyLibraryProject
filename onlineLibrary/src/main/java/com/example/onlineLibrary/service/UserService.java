package com.example.onlineLibrary.service;

import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.enums.RoleName;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


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
    public void deleteUser(Long userId) {
        User currentUser = getCurrentUser();
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getId().equals(userToDelete.getId())) {
            throw new IllegalStateException("Admin ne može obrisati samog sebe");
        }

        userRepository.delete(userToDelete);
    }
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Korisnik ne postoji"));
        user.setBlocked(true);
        userRepository.save(user);
    }
}
