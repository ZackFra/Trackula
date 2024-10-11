package com.trackula.track.dto;

public class CreateTimerEntryCategoryRequest {
    private Long categoryId;
    private Long timerEntryId;

    public Long getTimerEntryId() {
        return timerEntryId;
    }

    public void setTimerEntryId(Long timerEntryId) {
        this.timerEntryId = timerEntryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
