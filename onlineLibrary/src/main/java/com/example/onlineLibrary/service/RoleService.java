package com.example.onlineLibrary.service;


import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    // ==============================
    // Dohvata sve role
    // ==============================
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    // ==============================
    // Čuvanje ili kreiranje nove role
    // ==============================
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    // ==============================
    // Dohvata role po ID-ju
    // ==============================
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    // ==============================
    // Dohvata role po imenu (enum)
    // ==============================
    public Optional<Role> findByName(RoleName name) {
        return roleRepository.findByName(name);
    }

    // ==============================
    // Brisanje role
    // ==============================
    public void delete(Role role) {
        if (role != null) {
            roleRepository.delete(role);
        }
    }
}

