package com.example.onlineLibrary.config;

import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.repository.RoleRepository;
import com.example.onlineLibrary.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // ==============================
        // 1️⃣ Kreiraj role-e ako ne postoje
        // ==============================
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder()
                    .name(RoleName.ADMIN)
                    .build();

            Role userRole = Role.builder()
                    .name(RoleName.USER)
                    .build();

            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            System.out.println("Default roles created.");
        }

        // ==============================
        // 2️⃣ Kreiraj admin user
        // ==============================
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));



            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123")) // promeni po želji
                    .active(true)
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            System.out.println("Admin user created.");
        }

        // ==============================
        // 3️⃣ Kreiraj regular user
        // ==============================
        if (userRepository.findByUsername("user").isEmpty()) {
            Role userRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("USER role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);

            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123")) // promeni po želji
                    .active(true)
                    .roles(roles)
                    .build();

            userRepository.save(user);
            System.out.println("Regular user created.");
        }
    }
}