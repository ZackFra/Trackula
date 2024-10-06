package com.trackula.track.repository;

import com.trackula.track.model.TimerEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TimerEntryRepository extends CrudRepository<TimerEntry, Long> {
    Optional<TimerEntry> findByIdAndOwner(Long id, String owner);
    boolean existsByIdAndOwner(Long id, String owner);
    Iterable<TimerEntry> findAllByOwner(String owner);
}