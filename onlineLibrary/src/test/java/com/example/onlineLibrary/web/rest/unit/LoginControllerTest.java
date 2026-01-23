package com.example.onlineLibrary.web.rest.unit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void loginPageShowsError() throws Exception {
        mockMvc.perform(get("/users/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                // proveravamo da li je tekst iz error prikazan u HTML-u
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Pogrešno korisničko ime ili lozinka")));
    }

    @Test
    void successfulLoginRedirects() throws Exception {
        mockMvc.perform(formLogin("/users/login")
                        .user("admin").password("adminpass"))
                .andExpect(status().is3xxRedirection()); // obično na / ili /home
    }

    @Test
    void failedLoginRedirectsToLoginWithError() throws Exception {
        mockMvc.perform(formLogin("/users/login")
                        .user("admin").password("wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login?error"));
    }
}

