package com.example.LibraryMicroservice.config;

import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        return mapper;
    }
}