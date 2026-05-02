package com.pipelinex.leads;

import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;

import java.time.Instant;
import java.time.LocalDate;

public record LeadListItemView(Long id,
                               String leadName,
                               String companyName,
                               String phone,
                               String email,
                               String leadSource,
                               String assignedRepName,
                               LeadStage currentStage,
                               LocalDate nextFollowUpDate,
                               FollowUpStatus nextFollowUpStatus,
                               LocalDate lastActivityAt,
                               Instant updatedAt,
                               boolean archived,
                               boolean overdue) {
}
