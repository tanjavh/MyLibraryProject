package com.example.LibraryMicroservice.web.controller;


import com.example.LibraryMicroservice.model.entity.Category;
import com.example.LibraryMicroservice.model.enums.CategoryName;
import com.example.LibraryMicroservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // =========================
    // Lista svih kategorija
    // =========================
    @GetMapping
    public String allCategories(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "all-categories";
    }

    // =========================
    // Forma za kreiranje nove kategorije
    // =========================
    @GetMapping("/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", Arrays.asList(CategoryName.values())); // za select u formi
        return "create-category";
    }

    // =========================
    // Kreiranje nove kategorije
    // =========================
    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    // =========================
    // Brisanje kategorije
    // =========================
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.findById(id).ifPresent(categoryService::delete);
        return "redirect:/categories";
    }
}
