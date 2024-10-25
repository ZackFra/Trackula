package com.trackula.track.repository;

import com.trackula.track.model.TimerEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class TimerEntryJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public TimerEntryJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public TimerEntry save(TimerEntry timerEntry) {
        String sql = "INSERT INTO timer_entry(owner, time_tracked) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );
            ps.setString(1, timerEntry.owner());
            ps.setLong(2, timerEntry.timeTracked());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return new TimerEntry(
                generatedId,
                timerEntry.owner(),
                timerEntry.timeTracked()
        );
    }
}
