package com.pipelinex.pipeline;

import com.pipelinex.shared.domain.LeadStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "lead_stage_history")
public class LeadStageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_stage", nullable = false, length = 30)
    private LeadStage previousStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_stage", nullable = false, length = 30)
    private LeadStage newStage;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    public Long getId() {
        return id;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public LeadStage getPreviousStage() {
        return previousStage;
    }

    public void setPreviousStage(LeadStage previousStage) {
        this.previousStage = previousStage;
    }

    public LeadStage getNewStage() {
        return newStage;
    }

    public void setNewStage(LeadStage newStage) {
        this.newStage = newStage;
    }

    public Long getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    @PrePersist
    void onCreate() {
        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }
}
