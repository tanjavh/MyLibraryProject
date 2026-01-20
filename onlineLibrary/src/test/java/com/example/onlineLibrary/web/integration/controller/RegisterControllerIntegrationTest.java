package com.example.onlineLibrary.web.integration.controller;

import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegisterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUserSuccessfully() throws Exception {
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("email", "testuser@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        User user = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new AssertionError("User not saved"));

        assertThat(user.isActive()).isTrue();
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.getPassword()).isNotEqualTo("password123"); // enkodovana
    }
    @Test
    void testRegisterPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("email", "testuser@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "differentPassword")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        assertThat(userRepository.findByUsername("testuser")).isEmpty();
    }

}
