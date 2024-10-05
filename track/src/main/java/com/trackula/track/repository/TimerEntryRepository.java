package com.trackula.track.repository;

import com.trackula.track.model.TimerEntry;
import org.springframework.data.repository.CrudRepository;

public interface TimerEntryRepository extends CrudRepository<TimerEntry, Long> {

}