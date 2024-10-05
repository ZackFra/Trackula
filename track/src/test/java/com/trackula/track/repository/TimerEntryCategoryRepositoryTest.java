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
    void ensureFindByIdReturnsAnOptional() {
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepository.findById(0L);
        assertThat(timerEntryCategoryOptional).isNotNull();
    }

    @Test
    void ensureFindByIdReturnsATimerEntryCategory() {
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepository.findById(0L);
        assertThat(timerEntryCategoryOptional.isPresent()).isTrue();
    }

    @Test
    void ensureDeleteByCategoryIdWorksWithValidId() {
        boolean isExceptionThrown = false;
        String exMessage = "failed";
        try {
            timerEntryCategoryRepository.deleteByCategoryId(0L);
        } catch(Exception e) {
            exMessage = e.getMessage();
            isExceptionThrown = true;
        }

        assertThat(isExceptionThrown)
                .withFailMessage(exMessage)
                .isFalse();
    }
}