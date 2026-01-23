package com.example.onlineLibrary.web.rest.integration;

import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.repository.RoleRepository;
import com.example.onlineLibrary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Kreiramo USER rolu
        Role userRole = Role.builder()
                .name(RoleName.USER)
                .build();
        roleRepository.save(userRole);

        // Kreiramo ADMIN rolu
        Role adminRole = Role.builder()
                .name(RoleName.ADMIN)
                .build();
        roleRepository.save(adminRole);

        // Kreiramo test korisnika
        User user = User.builder()
                .username("restuser")
                .email("rest@example.com")
                .password("encoded-password") // simulacija enkriptovane lozinke
                .active(true)
                .blocked(false)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }

    // ✅ Pozitivan scenario: korisnik postoji
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByUsername_success() throws Exception {
        mockMvc.perform(get("/api/users/by-username/restuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("restuser"))
                .andExpect(jsonPath("$.email").value("rest@example.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.blocked").value(false))
                // 🔹 koristimo hasItem jer set može promeniti redosled
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").value(hasItem("USER")));
    }

    // ❌ Negativan scenario: korisnik ne postoji
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByUsername_notFound() throws Exception {
        mockMvc.perform(get("/api/users/by-username/unknownuser"))
                .andExpect(status().isNotFound()) // 👈 promenjeno
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nešto je pošlo po zlu")));
    }
}
