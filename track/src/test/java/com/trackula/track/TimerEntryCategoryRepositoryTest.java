package com.trackula.track;

import com.trackula.model.TimerEntryCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@SpringBootTest
public class TimerEntryCategoryRepositoryTest {
    @Autowired
    TimerEntryCategoryRepository timerEntryCategoryRepo;

    @Test
    void ensureFindByIdReturnsAnOptional() {
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepo.findById(1L);
        assertThat(timerEntryCategoryOptional).isNotNull();
    }

    @Test
    void ensureFindByIdReturnsATimerEntryCategory() {
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepo.findById(1L);
        assertThat(timerEntryCategoryOptional.isPresent()).isTrue();
    }
}
