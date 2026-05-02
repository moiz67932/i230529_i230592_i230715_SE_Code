package com.pipelinex.followups;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class FollowUpForm {

    @NotNull
    private LocalDate dueDate;

    private String completionNote;

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getCompletionNote() {
        return completionNote;
    }

    public void setCompletionNote(String completionNote) {
        this.completionNote = completionNote;
    }
}
