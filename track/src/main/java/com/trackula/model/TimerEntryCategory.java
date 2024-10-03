package com.trackula.model;

import org.springframework.data.annotation.Id;

public record TimerEntryCategory(
        @Id Long id,
        Long categoryId,
        Long timerEntryId
) {

}
