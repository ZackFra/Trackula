package com.trackula.track.repository;

import com.trackula.track.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    boolean existsByName(String name);
    Category findByName(String name);
}