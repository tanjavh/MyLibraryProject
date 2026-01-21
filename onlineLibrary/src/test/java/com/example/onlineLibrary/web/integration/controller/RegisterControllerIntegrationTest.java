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
                .andExpect(redirectedUrl("/users/login"));

        User user = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new AssertionError("User not saved"));

        assertThat(user.isActive()).isTrue();
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.getPassword()).isNotEqualTo("password123"); // enkodovana
    }

//    @Test
//    void testRegisterUserSuccessfully() throws Exception {
//        mockMvc.perform(post("/users/register")
//                        .with(csrf())
//                        .param("username", "testuser")
//                        .param("email", "test@example.com")
//                        .param("password", "password123")
//                        .param("confirmPassword", "password123"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/users/login"));
//
//        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
//    }
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
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors(
                        "userRegisterDto", "confirmPassword"
                ));

        assertThat(userRepository.findByUsername("testuser")).isEmpty();
    }
    @Test
    void testRegisterWithoutEmail_shouldFailValidation() throws Exception {
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userRegisterDto", "email"));
    }
    @Test
    void testRegisterInvalidEmail_shouldFail() throws Exception {
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("email", "not-an-email")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model()
                        .attributeHasFieldErrors("userRegisterDto", "email"));
    }
    @Test
    void testRegisterDuplicateEmail_shouldFail() throws Exception {
        User existing = User.builder()
                .username("existing")
                .email("duplicate@example.com")
                .password("encoded")
                .active(true)
                .blocked(false)
                .build();

        userRepository.save(existing);

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "duplicate@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model()
                        .attributeHasFieldErrors("userRegisterDto", "email"));
    }
//    @Test
//    void testRegisterPasswordsDoNotMatch() throws Exception {
//        mockMvc.perform(post("/users/register")
//                        .with(csrf())
//                        .param("username", "testuser")
//                        .param("email", "test@example.com")
//                        .param("password", "password123")
//                        .param("confirmPassword", "wrong"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("register"))
//                .andExpect(model()
//                        .attributeHasFieldErrors("userRegisterDto", "confirmPassword"));
//    }




}
