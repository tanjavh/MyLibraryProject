package com.example.onlineLibrary.model.dto;


import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {
    private String username;
    @Email
    private String email;
    private String password;
    private Set<String> roles;
}

