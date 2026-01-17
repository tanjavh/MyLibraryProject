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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    // ==============================
    // SVI KORISNICI (ADMIN)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "all-users";
    }

    // ==============================
    // KREIRANJE KORISNIKA (ADMIN)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "create-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto dto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "create-user";
        }

        Role userRole = roleService.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .active(true)
                .roles(Collections.singleton(userRole))
                .build();

        userService.save(user);
        return "redirect:/users";
    }

    // ==============================
    // BRISANJE KORISNIKA (ADMIN)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {

        // Ne dozvoli adminu da obriše samog sebe
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Trenutni korisnik nije pronađen"));

        if (currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "Ne možete da obrišete samog sebe!");
            return "redirect:/users";
        }

        userService.getUserById(id).ifPresent(userService::delete);
        return "redirect:/users";
    }

    // ==============================
    // LOGIN
    // ==============================
    @GetMapping("/login")
    public String loginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "login";
    }

    // ==============================
    // REGISTRACIJA
    // ==============================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto dto,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .active(true)
                .blocked(false)
                .build();

        userService.register(user); // registracija sa default USER rolom

        return "redirect:/login";
    }
}
