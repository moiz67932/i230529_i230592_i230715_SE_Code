package com.pipelinex.assignments;

import jakarta.validation.constraints.NotNull;

public class AssignmentForm {

    @NotNull
    private Long targetRepId;

    private String note;

    public Long getTargetRepId() {
        return targetRepId;
    }

    public void setTargetRepId(Long targetRepId) {
        this.targetRepId = targetRepId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
