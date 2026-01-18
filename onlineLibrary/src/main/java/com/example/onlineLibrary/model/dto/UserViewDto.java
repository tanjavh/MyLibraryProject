package com.example.onlineLibrary.model.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserViewDto {

    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean active;
    private boolean blocked;
//    private Set<String> roles; // ["USER"], ["ADMIN"]
    private boolean hasActiveLoans; // true ako korisnik ima aktivne pozajmice
}
