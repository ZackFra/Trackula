package com.trackula.repository;

import com.trackula.model.TimerEntryCategory;
import com.trackula.track.TrackApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@SpringBootTest(classes= TrackApplication.class)
@EnableAutoConfiguration
public class TimerEntryCategoryRepositoryTest {
    @Autowired
    TimerEntryCategoryRepository timerEntryCategoryRepo;

    @Test
    void ensureFindByIdReturnsAnOptional() {
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepo.findById(0L);
        assertThat(timerEntryCategoryOptional).isNotNull();
    }

    @Test
    void ensureFindByIdReturnsATimerEntryCategory() {
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepo.findById(0L);
        assertThat(timerEntryCategoryOptional.isPresent()).isTrue();
    }
}
