package com.example.onlineLibrary.web.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BooksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestTemplate restTemplate; // ovo će mockovati REST poziv

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateBook_asAdmin_shouldRedirect() throws Exception {
        // Mock REST poziv
        when(restTemplate.postForObject(anyString(), any(), eq(Void.class)))
                .thenReturn(null);

        mockMvc.perform(post("/books/create")
                        .with(csrf())
                        .param("title", "Test Book")
                        .param("author", "Test Author")
                        .param("category", "FICTION")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        // Provera da li je restTemplate pozvan
        verify(restTemplate).postForObject(anyString(), any(), eq(Void.class));
    }
}

