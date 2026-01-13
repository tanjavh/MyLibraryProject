package com.example.onlineLibrary.web.controller;


import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // ==============================
    // PRIKAZ SVIH ROLA
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String allRoles(Model model) {
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        return "roles/all-roles"; // Thymeleaf template
    }

    // ==============================
    // FORMA ZA KREIRANJE NOVE ROLE
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createRoleForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roleNames", RoleName.values()); // Enum za select listu
        return "roles/create-role";
    }

    // ==============================
    // KREIRANJE NOVE ROLE
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createRole(@ModelAttribute Role role) {
        roleService.save(role);
        return "redirect:/roles";
    }

    // ==============================
    // FORMA ZA IZMENU ROLE
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editRoleForm(@PathVariable Long id, Model model) {
        Role role = roleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        model.addAttribute("role", role);
        model.addAttribute("roleNames", RoleName.values());
        return "roles/edit-role";
    }

    // ==============================
    // IZMJENA ROLE
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String editRole(@PathVariable Long id, @ModelAttribute Role roleDetails) {
        Role role = roleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(roleDetails.getName());
        roleService.save(role);

        return "redirect:/roles";
    }

    // ==============================
    // BRISANJE ROLE
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        Role role = roleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roleService.delete(role);
        return "redirect:/roles";
    }
}

