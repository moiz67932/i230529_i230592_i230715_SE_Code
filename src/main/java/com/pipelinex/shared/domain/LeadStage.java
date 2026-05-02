package com.pipelinex.shared.domain;

public enum LeadStage {
    NEW,
    CONTACTED,
    QUALIFIED,
    PROPOSAL,
    WON,
    LOST;

    public boolean terminal() {
        return this == WON || this == LOST;
    }
}
