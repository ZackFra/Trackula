package com.trackula.track.repository;

import com.trackula.track.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class CategoryJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public CategoryJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Category save(Category category) {
        String sql = "INSERT INTO category(owner, name) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            ps.setString(1, category.owner());
            ps.setString(2, category.name());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return new Category(
                generatedId,
                category.owner(),
                category.name()
        );
    }
}
