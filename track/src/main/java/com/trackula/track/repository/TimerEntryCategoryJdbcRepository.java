package com.trackula.track.repository;

import com.trackula.track.model.Category;
import com.trackula.track.model.TimerEntry;
import com.trackula.track.model.TimerEntryCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class TimerEntryCategoryJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public TimerEntryCategoryJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TimerEntryCategory save(TimerEntryCategory timerEntryCategory) {
        String sql = "INSERT INTO timer_entry_category(owner, category_id, timer_entry_id) VALUES(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            ps.setString(1, timerEntryCategory.owner());
            ps.setLong(2, timerEntryCategory.categoryId());
            ps.setLong(3, timerEntryCategory.timerEntryId());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return new TimerEntryCategory(
                generatedId,
                timerEntryCategory.timerEntryId(),
                timerEntryCategory.categoryId(),
                timerEntryCategory.owner()
        );
    }
}
