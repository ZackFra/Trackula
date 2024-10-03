package com.trackula.track;

import com.trackula.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepo;

    @Test
    void ensureFindByIdReturnsAnOptional() {
        Optional<Category> categoryOptional = categoryRepo.findById(1L);
        assertThat(categoryOptional).isNotNull();
    }

    @Test
    void ensureFindByIdReturnACategory() {
        Optional<Category> categoryOptional = categoryRepo.findById(1L);
        boolean isPresent = categoryOptional.isPresent();
        assertThat(isPresent).isTrue();
    }
}
