package com.pipelinex.activities;

import com.pipelinex.shared.domain.ActivityType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ActivityForm {

    @NotNull
    private ActivityType activityType;

    @NotNull
    private LocalDate activityDate;

    private String notes;

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
