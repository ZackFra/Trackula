package com.trackula.track.repository;

import com.trackula.track.model.TimerEntryCategory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TimerEntryCategoryRepository extends CrudRepository<TimerEntryCategory, Long> {
    @Query("DELETE FROM timer_entry_category WHERE category_id = :categoryId")
    @Modifying
    void deleteByCategoryId(Long categoryId);

    @Query("DELETE FROM timer_entry_category WHERE timer_entry_id = :timerEntryId")
    @Modifying
    void deleteByTimerEntryId(Long timerEntryId);

    boolean existsByCategoryIdAndTimerEntryId(Long categoryId, Long timerEntryId);
    Iterable<TimerEntryCategory> findAllByTimerEntryId(Long timerEntryId);
}