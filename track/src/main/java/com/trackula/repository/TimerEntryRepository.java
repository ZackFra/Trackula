package com.trackula.repository;

import com.trackula.model.TimerEntry;
import org.springframework.data.repository.CrudRepository;

public interface TimerEntryRepository extends CrudRepository<TimerEntry, Long> {

}
