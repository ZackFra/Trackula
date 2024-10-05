package com.trackula.track.controller;

import com.trackula.track.model.Category;
import com.trackula.track.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategories(@PathVariable Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Category category = categoryOptional.get();
        return ResponseEntity.ok(category);
    }
}
