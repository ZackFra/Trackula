package com.trackula.model;

import org.springframework.data.annotation.Id;

public record TimerEntry(
        @Id Long id
) {
    
}