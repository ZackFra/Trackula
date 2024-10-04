package com.trackula.controller;

import com.trackula.repository.CategoryRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    CategoryRepository categoryRepository;
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
