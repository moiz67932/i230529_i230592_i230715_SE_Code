package com.pipelinex.activities;

import com.pipelinex.leads.Lead;
import com.pipelinex.leads.LeadService;
import com.pipelinex.shared.error.BusinessRuleException;
import com.pipelinex.shared.security.CurrentUserAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ActivityService {

    private final LeadService leadService;
    private final LeadActivityRepository activityRepository;
    private final CurrentUserAccessor currentUserAccessor;

    public ActivityService(LeadService leadService, LeadActivityRepository activityRepository, CurrentUserAccessor currentUserAccessor) {
        this.leadService = leadService;
        this.activityRepository = activityRepository;
        this.currentUserAccessor = currentUserAccessor;
    }

    public void logActivity(Long leadId, ActivityForm form) {
        if (form.getActivityDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Activity date cannot be in the future.");
        }
        Lead lead = leadService.requireLeadForCurrentUser(leadId);
        LeadActivity activity = new LeadActivity();
        activity.setLeadId(lead.getId());
        activity.setActivityType(form.getActivityType());
        activity.setActivityDate(form.getActivityDate());
        activity.setNotes(form.getNotes());
        activity.setLoggedBy(currentUserAccessor.requireUser().getId());
        activityRepository.save(activity);
        leadService.refreshLeadSummary(leadId);
    }
}
