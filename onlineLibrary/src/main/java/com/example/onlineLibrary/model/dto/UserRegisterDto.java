package com.example.onlineLibrary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {
    private Long id;
    private String username;
    private String email;
    private String password;// u UserService-u je već enkodovan
    private String confirmPassword;
    private boolean active;
    private boolean blocked;
    private Set<String> roles; // npr. ["USER"], ["ADMIN"]
}
