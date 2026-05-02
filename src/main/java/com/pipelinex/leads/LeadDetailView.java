package com.pipelinex.leads;

import com.pipelinex.activities.ActivityForm;
import com.pipelinex.activities.LeadActivity;
import com.pipelinex.assignments.AssignmentForm;
import com.pipelinex.assignments.LeadAssignmentHistory;
import com.pipelinex.followups.FollowUpForm;
import com.pipelinex.followups.LeadFollowUp;
import com.pipelinex.pipeline.LeadStageHistory;
import com.pipelinex.pipeline.StageUpdateForm;
import com.pipelinex.shared.domain.LeadStage;
import com.pipelinex.users.UserOptionView;

import java.util.List;
import java.util.Map;

public record LeadDetailView(Lead lead,
                             String assignedRepName,
                             String createdByName,
                             String updatedByName,
                             Map<Long, String> userNames,
                             List<LeadAssignmentHistory> assignmentHistory,
                             List<LeadStageHistory> stageHistory,
                             List<LeadActivity> activities,
                             List<LeadFollowUp> followUps,
                             List<UserOptionView> repOptions,
                             AssignmentForm assignmentForm,
                             StageUpdateForm stageUpdateForm,
                             ActivityForm activityForm,
                             FollowUpForm followUpForm,
                             boolean admin,
                             boolean canEdit,
                             LeadStage[] stageOptions) {
}
