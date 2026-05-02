package com.pipelinex.followups;

import com.pipelinex.leads.Lead;
import com.pipelinex.leads.LeadService;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import com.pipelinex.shared.error.BusinessRuleException;
import com.pipelinex.shared.error.NotFoundException;
import com.pipelinex.shared.security.CurrentUserAccessor;
import com.pipelinex.users.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FollowUpService {

    private final LeadFollowUpRepository followUpRepository;
    private final LeadService leadService;
    private final CurrentUserAccessor currentUserAccessor;
    private final UserRepository userRepository;

    public FollowUpService(LeadFollowUpRepository followUpRepository,
                           LeadService leadService,
                           CurrentUserAccessor currentUserAccessor,
                           UserRepository userRepository) {
        this.followUpRepository = followUpRepository;
        this.leadService = leadService;
        this.currentUserAccessor = currentUserAccessor;
        this.userRepository = userRepository;
    }

    @Transactional
    public void schedule(Long leadId, FollowUpForm form) {
        if (!form.getDueDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Follow-up date must be in the future.");
        }
        Lead lead = leadService.requireLeadForCurrentUser(leadId);
        LeadFollowUp followUp = new LeadFollowUp();
        followUp.setLeadId(leadId);
        followUp.setAssignedRepId(lead.getAssignedToUserId());
        followUp.setDueDate(form.getDueDate());
        followUp.setStatus(FollowUpStatus.PENDING);
        followUp.setScheduledBy(currentUserAccessor.requireUser().getId());
        followUpRepository.save(followUp);
        leadService.refreshLeadSummary(leadId);
    }

    @Transactional
    public void reschedule(Long id, FollowUpForm form) {
        if (!form.getDueDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Follow-up date must be in the future.");
        }
        LeadFollowUp followUp = findAuthorized(id);
        followUp.setDueDate(form.getDueDate());
        followUp.setCompletionNote(form.getCompletionNote());
        followUpRepository.save(followUp);
        leadService.refreshLeadSummary(followUp.getLeadId());
    }

    @Transactional
    public void complete(Long id, String note) {
        LeadFollowUp followUp = findAuthorized(id);
        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUp.setCompletedAt(Instant.now());
        followUp.setCompletedBy(currentUserAccessor.requireUser().getId());
        followUp.setCompletionNote(note);
        followUpRepository.save(followUp);
        leadService.refreshLeadSummary(followUp.getLeadId());
    }

    public List<OverdueFollowUpItemView> overdueForCurrentUser() {
        Long repId = currentUserAccessor.requireAuthUser().isAdmin() ? null : currentUserAccessor.requireAuthUser().getId();
        return followUpRepository.findOverdue(repId, Sort.by(Sort.Order.asc("dueDate")))
                .stream()
                .map(item -> {
                    Lead lead = leadService.requireLead(item.getLeadId());
                    String repName = item.getAssignedRepId() == null ? "Unassigned" :
                            userRepository.findById(item.getAssignedRepId()).map(user -> user.getFullName()).orElse("Unknown");
                    return new OverdueFollowUpItemView(item.getId(), lead.getId(), lead.getLeadName(), lead.getCompanyName(), repName,
                            item.getDueDate(), ChronoUnit.DAYS.between(item.getDueDate(), LocalDate.now()));
                })
                .toList();
    }

    public long overdueCountForCurrentUser() {
        Long repId = currentUserAccessor.requireAuthUser().isAdmin() ? null : currentUserAccessor.requireAuthUser().getId();
        return followUpRepository.countOverdue(repId);
    }

    @Transactional
    public void closePendingForTerminalStage(Lead lead, LeadStage stage) {
        FollowUpStatus status = stage == LeadStage.WON ? FollowUpStatus.COMPLETED : FollowUpStatus.CANCELLED;
        String note = stage == LeadStage.WON ? "Auto-closed because lead moved to WON." : "Auto-closed because lead moved to LOST.";
        for (LeadFollowUp followUp : followUpRepository.findPendingForLead(lead.getId())) {
            followUp.setStatus(status);
            followUp.setCompletedBy(currentUserAccessor.requireUser().getId());
            followUp.setCompletedAt(Instant.now());
            followUp.setCompletionNote(note);
        }
        leadService.refreshLeadSummary(lead.getId());
    }

    private LeadFollowUp findAuthorized(Long id) {
        LeadFollowUp followUp = followUpRepository.findById(id).orElseThrow(() -> new NotFoundException("Follow-up not found."));
        leadService.requireLeadForCurrentUser(followUp.getLeadId());
        return followUp;
    }
}
