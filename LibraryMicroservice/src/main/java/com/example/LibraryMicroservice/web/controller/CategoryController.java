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


    @GetMapping
    public String allCategories(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "all-categories";
    }


    @GetMapping("/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", Arrays.asList(CategoryName.values()));
        return "create-category";
    }


    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }


    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.findById(id).ifPresent(categoryService::delete);
        return "redirect:/categories";
    }
}
