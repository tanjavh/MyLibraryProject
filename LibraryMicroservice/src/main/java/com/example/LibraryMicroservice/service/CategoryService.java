package com.example.LibraryMicroservice.service;


import com.example.LibraryMicroservice.model.entity.Category;
import com.example.LibraryMicroservice.model.enums.CategoryName;
import com.example.LibraryMicroservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public void delete(Category category) {
        if (category != null) categoryRepository.delete(category);
    }

    public Optional<Category> findByName(CategoryName name) {
        return categoryRepository.findByName(name);
    }



}
