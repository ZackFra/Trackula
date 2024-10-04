package com.trackula.repository;

import com.trackula.model.Category;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
