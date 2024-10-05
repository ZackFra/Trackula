package com.trackula.track.repository;

import com.trackula.track.TrackApplication;
import com.trackula.track.model.TimerEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Optional;

@SpringBootTest(classes= TrackApplication.class)
public class TimerEntryRepositoryTest {
    @Autowired
    TimerEntryRepository timerEntryRepository;

    @Test
    public void ensureFindByIdReturnsAnOptional() {
        Optional<TimerEntry> timerEntryOptional = timerEntryRepository.findById(0L);
        assertThat(timerEntryOptional).isNotNull();
    }

    @Test
    public void ensureFindByIdReturnsATimerEntry() {
        Optional<TimerEntry> timerEntryOptional = timerEntryRepository.findById(0L);
        assertThat(timerEntryOptional.isPresent()).isTrue();
    }
}