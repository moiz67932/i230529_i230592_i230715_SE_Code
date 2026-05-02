package com.pipelinex.followups;

import java.time.LocalDate;

public record OverdueFollowUpItemView(Long id,
                                      Long leadId,
                                      String leadName,
                                      String companyName,
                                      String assignedRepName,
                                      LocalDate dueDate,
                                      long daysOverdue) {
}
