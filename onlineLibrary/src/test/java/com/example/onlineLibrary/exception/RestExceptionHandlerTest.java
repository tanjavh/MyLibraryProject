package com.example.onlineLibrary.exception;

import com.example.onlineLibrary.config.SecurityConfig;
import com.example.onlineLibrary.service.UserService;
import com.example.onlineLibrary.web.rest.TestExceptionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestExceptionController.class)
@Import({SecurityConfig.class, RestExceptionHandler.class})
class RestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser // Obavezno za Security da ne dobiješ 401
    void runtimeException_shouldReturn404WithMessage() throws Exception {

        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Nešto je pošlo po zlu"));
    }
}
