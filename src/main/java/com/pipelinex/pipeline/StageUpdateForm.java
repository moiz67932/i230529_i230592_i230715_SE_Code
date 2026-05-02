package com.pipelinex.pipeline;

import com.pipelinex.shared.domain.LeadStage;
import jakarta.validation.constraints.NotNull;

public class StageUpdateForm {

    @NotNull
    private LeadStage newStage;

    public LeadStage getNewStage() {
        return newStage;
    }

    public void setNewStage(LeadStage newStage) {
        this.newStage = newStage;
    }
}
