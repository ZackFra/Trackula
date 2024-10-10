package com.trackula.track.repository;

import com.trackula.track.TrackApplication;
import com.trackula.track.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class)
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void ensureFindAllReturnsAnIterable() {
        Iterable<Category> categoryIterable = categoryRepository.findAll();
        assertThat(categoryIterable).isNotNull();
    }
}