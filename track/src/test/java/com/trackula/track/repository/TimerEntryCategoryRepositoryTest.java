package com.trackula.track.repository;

import com.trackula.track.model.TimerEntryCategory;
import com.trackula.track.TrackApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@SpringBootTest(classes= TrackApplication.class)
public class TimerEntryCategoryRepositoryTest {
    @Autowired
    TimerEntryCategoryRepository timerEntryCategoryRepository;

    @Test
    void ensureFindAllReturnsAnIterable() {
        Iterable<TimerEntryCategory> timerEntryCategoryIterable = timerEntryCategoryRepository.findAll();
        assertThat(timerEntryCategoryIterable).isNotNull();
    }
}