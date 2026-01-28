package com.example.onlineLibrary.web.controller;


import com.example.onlineLibrary.model.entity.Role;
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
        return "roles/all-roles";
    }


}

