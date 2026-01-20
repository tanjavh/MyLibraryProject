package com.example.onlineLibrary.web.rest;


import com.example.onlineLibrary.model.dto.UserRegisterDto;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    // GET /api/users/by-username/{username}
    @GetMapping("/by-username/{username}")
    public UserRegisterDto getByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username));

        return UserRegisterDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword()) // već enkodovan u UserService-u
                .active(user.isActive())
                .blocked(user.isBlocked())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
                        : null)
                .build();
    }

}

