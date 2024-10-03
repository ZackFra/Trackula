package com.trackula.track;

import com.trackula.model.Category;
import org.springframework.data.repository.CrudRepository;


public interface CategoryRepository extends CrudRepository<Category, Long> {
}
