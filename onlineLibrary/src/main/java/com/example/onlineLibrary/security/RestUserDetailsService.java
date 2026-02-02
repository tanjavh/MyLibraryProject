package com.example.onlineLibrary.security;

import com.example.onlineLibrary.model.dto.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestUserDetailsService implements UserDetailsService {

    private final RestTemplate restTemplate; // Bean RestTemplate mora postojati
    private final String userServiceUrl = "http://localhost:8082/api/users"; // URL UserService-a

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Poziv REST API-ja ka UserService-u da dobijemo DTO korisnika
        UserRegisterDto userDto = restTemplate.getForObject(
                userServiceUrl + "/by-username/" + username,
                UserRegisterDto.class
        );

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Ako roles nisu poslati (npr. null), dodeli default ROLE_USER
        List<GrantedAuthority> authorities;
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            authorities = userDto.getRoles().stream()
                    .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName)) // prefix ROLE_ je obavezan
                    .collect(Collectors.toList());
        }

        // Kreiranje Spring Security User objekta
        return new org.springframework.security.core.userdetails.User(
                userDto.getUsername(),
                userDto.getPassword(), // password mora biti šifrovano u UserService-u
                authorities
        );
    }
}
