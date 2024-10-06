package com.trackula.track.repository;

import com.trackula.track.TrackApplication;
import com.trackula.track.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class)
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void ensureFindByIdReturnsAnOptional() {
        Optional<Category> categoryOptional = categoryRepository.findById(0L);
        assertThat(categoryOptional).isNotNull();
    }

    @Test
    void ensureFindByIdReturnACategory() {
        Optional<Category> categoryOptional = categoryRepository.findById(0L);
        boolean isPresent = categoryOptional.isPresent();
        assertThat(isPresent).isTrue();
    }
}