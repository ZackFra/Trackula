package com.trackula.track.dto;

public class UpdateTimerEntryRequest {
    private Long timeTracked;
    private String owner;

    public Long getTimeTracked() {
        return timeTracked;
    }

    public void setTimeTracked(Long timeTracked) {
        this.timeTracked = timeTracked;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
