package com.trackula.repository;

import com.trackula.model.TimerEntry;
import com.trackula.track.TrackApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Optional;

@SpringBootTest(classes= TrackApplication.class)
@EnableAutoConfiguration
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
