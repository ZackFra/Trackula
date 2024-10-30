package com.trackula.track.dto;

public class CreateTimerEntryRequest {
    public Long getTimeTracked() {
        return timeTracked;
    }

    public void setTimeTracked(Long timeTracked) {
        this.timeTracked = timeTracked;
    }

    private Long timeTracked;
}
