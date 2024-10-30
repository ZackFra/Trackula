package com.trackula.track.controller;

import com.trackula.track.dto.CreateCategoryRequest;
import com.trackula.track.dto.UpdateCategoryRequest;
import com.trackula.track.model.Category;
import com.trackula.track.repository.CategoryJdbcRepository;
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
    private final CategoryJdbcRepository categoryJdbcRepository;

    public CategoryController(CategoryRepository categoryRepository, TimerEntryCategoryRepository timerEntryCategoryRepository, CategoryJdbcRepository categoryJdbcRepository) {
        this.categoryRepository = categoryRepository;
        this.timerEntryCategoryRepository = timerEntryCategoryRepository;
        this.categoryJdbcRepository = categoryJdbcRepository;
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
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Category category = categoryOptional.get();
        return ResponseEntity.ok(category);
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest, @CurrentOwner String owner) {
        if (createCategoryRequest.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        boolean isInDatabaseAlready = categoryRepository.existsByName(createCategoryRequest.getName());
        if (isInDatabaseAlready) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Category newCategory = categoryJdbcRepository.save(new Category(
                null,
                createCategoryRequest.getName(),
                owner
        ));
        URI uri = URI.create("/category/" + newCategory.id());
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(id);
        if(existingCategoryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Category existingCategory = existingCategoryOptional.get();
        String newName = existingCategory.name();
        String newOwner = existingCategory.owner();
        if(updateCategoryRequest.getName() != null) {
            newName = updateCategoryRequest.getName();
        }
        if(updateCategoryRequest.getOwner() != null) {
            newOwner = updateCategoryRequest.getOwner();
        }
        Category newCategory = new Category(
                id,
                newName,
                newOwner
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
