package com.trackula.track.model;

import org.springframework.data.annotation.Id;

public record TimerEntryCategory(@Id Long id, Long timerEntryId, Long categoryId, String owner) {
}
