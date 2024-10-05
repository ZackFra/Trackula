package com.trackula.repository;

import com.trackula.track.TrackApplication;
import com.trackula.track.model.Category;
import com.trackula.track.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class)
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepo;

    @Test
    void ensureFindByIdReturnsAnOptional() {
        Optional<Category> categoryOptional = categoryRepo.findById(0L);
        assertThat(categoryOptional).isNotNull();
    }

    @Test
    void ensureFindByIdReturnACategory() {
        Optional<Category> categoryOptional = categoryRepo.findById(0L);
        boolean isPresent = categoryOptional.isPresent();
        assertThat(isPresent).isTrue();
    }
}