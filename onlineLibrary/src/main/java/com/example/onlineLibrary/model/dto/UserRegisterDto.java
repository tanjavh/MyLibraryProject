package com.example.onlineLibrary.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Username je obavezan")
    private String username;

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email mora biti validan")
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 6, message = "Lozinka mora imati bar 6 karaktera")
    private String password;

    @NotBlank(message = "Potvrda lozinke je obavezna")
    private String confirmPassword;

    private boolean active;
    private boolean blocked;
    private Set<String> roles;
}
