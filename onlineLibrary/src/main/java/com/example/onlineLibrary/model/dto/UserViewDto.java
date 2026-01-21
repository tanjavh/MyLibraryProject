package com.example.onlineLibrary.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserViewDto {

    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String role;
    @NotBlank
    private boolean active;
    private boolean blocked;
//    private Set<String> roles; // ["USER"], ["ADMIN"]
    private boolean hasActiveLoans; // true ako korisnik ima aktivne pozajmice
}
