package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.model.dto.UserRegisterDto;
import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.service.RoleService;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    // ==============================
    // PRIKAZ SVIH KORISNIKA (ADMIN CRUD)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "all-users";
    }

    // ==============================
    // FORMA ZA KREIRANJE NOVOG KORISNIKA (ADMIN)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        model.addAttribute("allRoles", roleService.findAll());
        return "create-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto dto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "create-user";
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .active(true)
                .build();

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            Set<Role> roles = dto.getRoles().stream()
                    .map(roleName -> roleService.findByName(RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            Role defaultRole = roleService.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.setRoles(Collections.singleton(defaultRole));
        }

        userService.save(user);
        return "redirect:/users";
    }

    // ==============================
    // BRISANJE KORISNIKA (ADMIN)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.getUserById(id).ifPresent(userService::delete);
        return "redirect:/users";
    }

    @GetMapping("/login")
    public String loginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Već ulogovan korisnik, preusmeri na home
            return "redirect:/";
        }
        return "login"; // prikazuje login formu
    }

    // ==============================
    // REGISTRACIJA (PUBLIC)
    // ==============================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        model.addAttribute("allRoles", roleService.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto dto,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "register";
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .active(true)
                .build();

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            Set<Role> roles = dto.getRoles().stream()
                    .map(roleName -> roleService.findByName(RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            Role defaultRole = roleService.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.setRoles(Collections.singleton(defaultRole));
        }

        userService.save(user);
        return "redirect:/login";
    }
}
