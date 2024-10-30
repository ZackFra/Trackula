package com.trackula.track.repository;

import com.trackula.track.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface CategoryRepository extends CrudRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}