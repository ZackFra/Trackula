package com.trackula.track.controller;

import com.trackula.track.model.Category;
import com.trackula.track.repository.CategoryRepository;
import com.trackula.track.repository.TimerEntryCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

// TODO add error handling
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final TimerEntryCategoryRepository timerEntryCategoryRepository;
    public CategoryController(CategoryRepository categoryRepository, TimerEntryCategoryRepository timerEntryCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.timerEntryCategoryRepository = timerEntryCategoryRepository;
    }

    // TODO Pagification
    @GetMapping
    public ResponseEntity<List<Category>> getCategories(Principal principal) {
        Iterable<Category> categoryIterator = categoryRepository.findAll();
        List<Category> categories = StreamSupport.stream(categoryIterator.spliterator(), false)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> categoryOptional;
        categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Category category = categoryOptional.get();
        return ResponseEntity.ok(category);
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        if(category.id() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(category.name() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        boolean isInDatabaseAlready = categoryRepository.existsByName(category.name());
        if(isInDatabaseAlready) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Category newCategory = categoryRepository.save(category);
        URI uri = URI.create("/category/" + newCategory.id());
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(id);
        if(existingCategoryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Category existingCategory = existingCategoryOptional.get();
        if(category.id() != null && !existingCategory.id().equals(category.id())) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        if(existingCategory.name().equals(category.name())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .build();
        }
        Category newCategory = new Category(
                id,
                category.name(),
                "admin"
        );
        categoryRepository.save(newCategory);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean isInDatabase = categoryRepository.existsById(id);
        if (!isInDatabase) {
            return ResponseEntity.notFound().build();
        }
        timerEntryCategoryRepository.deleteByCategoryId(id);
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
