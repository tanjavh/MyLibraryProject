package com.example.onlineLibrary.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InternationalizationIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void indexPage_shouldBeRenderedInDefaultLanguage_forAnonymousUser() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(
                        containsString("Dobrodošli") // tekst iz messages.properties
                ));
    }

    @Test
    void indexPage_shouldBeRenderedInEnglish_whenLangIsEn() throws Exception {
        mockMvc.perform(get("/?lang=en"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(
                        containsString("Welcome") // tekst iz messages_en.properties
                ));
    }
}
