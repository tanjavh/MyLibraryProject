package com.example.onlineLibrary.exception;



import com.example.onlineLibrary.config.SecurityConfig;
import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.service.LoanService;
import com.example.onlineLibrary.service.UserService;
import com.example.onlineLibrary.web.controller.BookController;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookController.class)
@Import({RestExceptionHandler.class, SecurityConfig.class})
class LibraryMicroserviceDownTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    void whenLibraryMicroserviceIsDown_shouldReturn503() throws Exception {

        when(restTemplate.getForObject(
                anyString(),
                eq(BookInfoResponse[].class)
        )).thenThrow(new ResourceAccessException("Konekcija odbijena"));

        mockMvc.perform(get("/books"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(
                        "LibraryMicroservice trenutno nije dostupan!!!"
                ));
    }
}
