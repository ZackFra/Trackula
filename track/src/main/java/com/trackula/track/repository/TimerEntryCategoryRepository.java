package com.trackula.track.repository;

import com.trackula.track.model.TimerEntryCategory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface TimerEntryCategoryRepository extends CrudRepository<TimerEntryCategory, Long> {
    @Query("DELETE FROM timer_entry_category WHERE category_id = :categoryId")
    @Modifying
    void deleteByCategoryId(Long categoryId);
}