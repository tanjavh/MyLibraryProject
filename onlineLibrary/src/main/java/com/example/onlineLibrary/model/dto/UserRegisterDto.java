package com.example.onlineLibrary.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserRegisterDto {

    private Long id;

    @NotBlank(message = "{NotBlank.user.username}")
    @Size(min = 3, max = 20, message = "{Size.user.username}")
    private String username;

    @NotBlank(message = "{NotBlank.user.email}")
    @Email(message = "{Email.user.email}")
    private String email;

    @NotBlank(message = "{NotBlank.user.password}")
    @Size(min = 6, message = "{Size.user.password}")
    private String password;

    @NotBlank(message = "{NotBlank.user.confirmPassword}")
    private String confirmPassword;


    private boolean active;
    private boolean blocked;
    private Set<String> roles;
}
