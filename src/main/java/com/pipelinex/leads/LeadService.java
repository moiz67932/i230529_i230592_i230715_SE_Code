package com.pipelinex.leads;

import com.pipelinex.activities.ActivityForm;
import com.pipelinex.activities.LeadActivityRepository;
import com.pipelinex.assignments.AssignmentForm;
import com.pipelinex.assignments.LeadAssignmentHistory;
import com.pipelinex.assignments.LeadAssignmentHistoryRepository;
import com.pipelinex.followups.FollowUpForm;
import com.pipelinex.followups.LeadFollowUp;
import com.pipelinex.followups.LeadFollowUpRepository;
import com.pipelinex.pipeline.LeadStageHistory;
import com.pipelinex.pipeline.LeadStageHistoryRepository;
import com.pipelinex.pipeline.StageUpdateForm;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import com.pipelinex.shared.domain.Role;
import com.pipelinex.shared.domain.UserStatus;
import com.pipelinex.shared.error.BusinessRuleException;
import com.pipelinex.shared.error.NotFoundException;
import com.pipelinex.shared.security.CurrentUserAccessor;
import com.pipelinex.shared.util.NormalizationUtils;
import com.pipelinex.users.User;
import com.pipelinex.users.UserOptionView;
import com.pipelinex.users.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final LeadAssignmentHistoryRepository assignmentHistoryRepository;
    private final LeadStageHistoryRepository stageHistoryRepository;
    private final LeadActivityRepository activityRepository;
    private final LeadFollowUpRepository followUpRepository;
    private final CurrentUserAccessor currentUserAccessor;

    public LeadService(LeadRepository leadRepository,
                       UserRepository userRepository,
                       LeadAssignmentHistoryRepository assignmentHistoryRepository,
                       LeadStageHistoryRepository stageHistoryRepository,
                       LeadActivityRepository activityRepository,
                       LeadFollowUpRepository followUpRepository,
                       CurrentUserAccessor currentUserAccessor) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.assignmentHistoryRepository = assignmentHistoryRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.activityRepository = activityRepository;
        this.followUpRepository = followUpRepository;
        this.currentUserAccessor = currentUserAccessor;
    }

    private String toLikePattern(String query) {
        String trimmed = NormalizationUtils.trimToNull(query);
        return trimmed == null ? null : "%" + trimmed.toLowerCase() + "%";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<LeadListItemView> searchAdmin(String query,
                                              LeadStage stage,
                                              Long assignedRepId,
                                              String assignmentState,
                                              FollowUpStatus followUpStatus,
                                              Boolean archived,
                                              String leadSource) {
        String ls = NormalizationUtils.trimToNull(leadSource);
        ls = ls == null ? null : ls.toLowerCase();
        return leadRepository.searchAdmin(toLikePattern(query), stage, assignedRepId, assignmentState,
                        followUpStatus, archived == null ? Boolean.FALSE : archived, ls,
                        Sort.by(Sort.Order.desc("updatedAt")))
                .stream()
                .map(this::toListItem)
                .toList();
    }

    public List<LeadListItemView> searchMyLeads(String query,
                                                LeadStage stage,
                                                FollowUpStatus followUpStatus,
                                                boolean overdueOnly) {
        User user = currentUserAccessor.requireUser();
        return leadRepository.searchRep(user.getId(), toLikePattern(query), stage, followUpStatus, overdueOnly, Boolean.FALSE,
                        Sort.by(Sort.Order.asc("nextFollowUpDate"), Sort.Order.desc("updatedAt")))
                .stream()
                .map(this::toListItem)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Lead createLead(LeadForm form) {
        validateLead(form, null);
        User actor = currentUserAccessor.requireUser();
        Lead lead = new Lead();
        applyForm(lead, form);
        lead.setCurrentStage(LeadStage.NEW);
        lead.setCreatedBy(actor.getId());
        lead.setUpdatedBy(actor.getId());
        if (form.getAssignedRepId() != null) {
            User rep = requireActiveRep(form.getAssignedRepId());
            lead.setAssignedToUserId(rep.getId());
            lead.setAssignedAt(Instant.now());
        }
        Lead saved = leadRepository.save(lead);
        if (saved.getAssignedToUserId() != null) {
            recordAssignment(saved.getId(), null, saved.getAssignedToUserId(), actor.getId(), "Assigned during lead creation.");
        }
        return saved;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateLead(Long id, LeadForm form) {
        Lead lead = requireLead(id);
        validateLead(form, id);
        User actor = currentUserAccessor.requireUser();
        applyForm(lead, form);
        lead.setUpdatedBy(actor.getId());
        leadRepository.save(lead);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void archive(Long id) {
        Lead lead = requireLead(id);
        User actor = currentUserAccessor.requireUser();
        lead.setArchived(true);
        lead.setUpdatedBy(actor.getId());
        leadRepository.save(lead);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restore(Long id) {
        Lead lead = requireLead(id);
        User actor = currentUserAccessor.requireUser();
        lead.setArchived(false);
        lead.setUpdatedBy(actor.getId());
        leadRepository.save(lead);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void assignLead(Long leadId, AssignmentForm form) {
        Lead lead = requireLead(leadId);
        User actor = currentUserAccessor.requireUser();
        User rep = requireActiveRep(form.getTargetRepId());
        Long previousRepId = lead.getAssignedToUserId();
        if (previousRepId != null && previousRepId.equals(rep.getId())) {
            throw new BusinessRuleException("Lead already assigned to this rep.");
        }
        lead.setAssignedToUserId(rep.getId());
        lead.setAssignedAt(Instant.now());
        lead.setUpdatedBy(actor.getId());
        leadRepository.save(lead);
        recordAssignment(leadId, previousRepId, rep.getId(), actor.getId(), NormalizationUtils.trimToNull(form.getNote()));
        transferPendingFollowUps(leadId, rep.getId());
    }

    public LeadDetailView adminWorkspace(Long leadId, List<UserOptionView> repOptions) {
        Lead lead = requireLead(leadId);
        return buildWorkspace(lead, true, repOptions);
    }

    public LeadDetailView repWorkspace(Long leadId) {
        Lead lead = requireLeadForCurrentUser(leadId);
        return buildWorkspace(lead, false, List.of());
    }

    public Lead requireLead(Long leadId) {
        return leadRepository.findById(leadId).orElseThrow(() -> new NotFoundException("Lead not found."));
    }

    public Lead requireLeadForCurrentUser(Long leadId) {
        User user = currentUserAccessor.requireUser();
        if (user.getRole() == Role.ADMIN) {
            return requireLead(leadId);
        }
        return leadRepository.findByIdAndAssignedToUserId(leadId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("You cannot access that lead."));
    }

    @Transactional
    public void refreshLeadSummary(Long leadId) {
        Lead lead = requireLead(leadId);
        List<LeadFollowUp> pending = followUpRepository.findPendingForLead(leadId);
        LeadFollowUp nextPending = pending.isEmpty() ? null : pending.getFirst();
        lead.setNextFollowUpDate(nextPending != null ? nextPending.getDueDate() : null);
        lead.setNextFollowUpStatus(nextPending != null ? nextPending.getStatus() : null);
        activityRepository.findByLeadId(leadId, Sort.by(Sort.Order.desc("activityDate")))
                .stream()
                .findFirst()
                .ifPresent(activity -> lead.setLastActivityAt(activity.getActivityDate()));
        leadRepository.save(lead);
    }

    private LeadDetailView buildWorkspace(Lead lead, boolean admin, List<UserOptionView> repOptions) {
        List<LeadAssignmentHistory> assignmentHistory = assignmentHistoryRepository.findByLeadId(lead.getId(), Sort.by(Sort.Order.desc("changedAt")));
        List<LeadStageHistory> stageHistory = stageHistoryRepository.findByLeadId(lead.getId(), Sort.by(Sort.Order.desc("changedAt")));
        var activities = activityRepository.findByLeadId(lead.getId(), Sort.by(Sort.Order.desc("activityDate"), Sort.Order.desc("createdAt")));
        var followUps = followUpRepository.findByLeadId(lead.getId(), Sort.by(Sort.Order.asc("dueDate"), Sort.Order.desc("createdAt")));
        Map<Long, String> userNames = buildUserNameMap(lead, assignmentHistory, stageHistory, activities, followUps);
        return new LeadDetailView(
                lead,
                userNames.get(lead.getAssignedToUserId()),
                userNames.get(lead.getCreatedBy()),
                userNames.get(lead.getUpdatedBy()),
                userNames,
                assignmentHistory,
                stageHistory,
                activities,
                followUps,
                repOptions,
                new AssignmentForm(),
                defaultStageForm(lead),
                defaultActivityForm(),
                defaultFollowUpForm(),
                admin,
                true,
                LeadStage.values()
        );
    }

    private Map<Long, String> buildUserNameMap(Lead lead,
                                               List<LeadAssignmentHistory> assignmentHistory,
                                               List<LeadStageHistory> stageHistory,
                                               List<com.pipelinex.activities.LeadActivity> activities,
                                               List<LeadFollowUp> followUps) {
        Map<Long, String> names = new HashMap<>();
        loadName(names, lead.getAssignedToUserId());
        loadName(names, lead.getCreatedBy());
        loadName(names, lead.getUpdatedBy());
        assignmentHistory.forEach(item -> {
            loadName(names, item.getPreviousRepId());
            loadName(names, item.getNewRepId());
            loadName(names, item.getChangedBy());
        });
        stageHistory.forEach(item -> loadName(names, item.getChangedBy()));
        activities.forEach(item -> loadName(names, item.getLoggedBy()));
        followUps.forEach(item -> {
            loadName(names, item.getAssignedRepId());
            loadName(names, item.getScheduledBy());
            loadName(names, item.getCompletedBy());
        });
        return names;
    }

    private void loadName(Map<Long, String> names, Long userId) {
        if (userId == null || names.containsKey(userId)) {
            return;
        }
        userRepository.findById(userId).ifPresent(user -> names.put(userId, user.getFullName()));
    }

    private void recordAssignment(Long leadId, Long previousRepId, Long newRepId, Long actorId, String note) {
        LeadAssignmentHistory history = new LeadAssignmentHistory();
        history.setLeadId(leadId);
        history.setPreviousRepId(previousRepId);
        history.setNewRepId(newRepId);
        history.setChangedBy(actorId);
        history.setNote(note);
        assignmentHistoryRepository.save(history);
    }

    private void transferPendingFollowUps(Long leadId, Long repId) {
        followUpRepository.findPendingForLead(leadId).forEach(item -> item.setAssignedRepId(repId));
    }

    private void applyForm(Lead lead, LeadForm form) {
        lead.setLeadName(NormalizationUtils.trimToNull(form.getLeadName()));
        lead.setPhone(NormalizationUtils.normalizePhone(form.getPhone()));
        lead.setEmail(NormalizationUtils.normalizeEmail(form.getEmail()));
        lead.setCompanyName(NormalizationUtils.trimToNull(form.getCompanyName()));
        lead.setLeadSource(NormalizationUtils.trimToNull(form.getLeadSource()));
        lead.setCompanyWebsite(NormalizationUtils.trimToNull(form.getCompanyWebsite()));
        lead.setJobTitle(NormalizationUtils.trimToNull(form.getJobTitle()));
        lead.setLinkedinUrl(NormalizationUtils.trimToNull(form.getLinkedinUrl()));
        lead.setNotes(NormalizationUtils.trimToNull(form.getNotes()));
    }

    private void validateLead(LeadForm form, Long leadId) {
        String phone = NormalizationUtils.normalizePhone(form.getPhone());
        String email = NormalizationUtils.normalizeEmail(form.getEmail());
        if (phone == null && email == null) {
            throw new BusinessRuleException("At least one of phone or email is required.");
        }
        leadRepository.findDuplicates(phone, email).stream()
                .filter(existing -> !existing.getId().equals(leadId))
                .findFirst()
                .ifPresent(existing -> {
                    throw new BusinessRuleException("A lead with the same phone or email already exists.");
                });
        if (form.getAssignedRepId() != null) {
            requireActiveRep(form.getAssignedRepId());
        }
    }

    private User requireActiveRep(Long repId) {
        User user = userRepository.findById(repId).orElseThrow(() -> new NotFoundException("Rep not found."));
        if (user.getRole() != Role.REP || user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleException("Only active reps can receive assignments.");
        }
        return user;
    }

    private LeadListItemView toListItem(Lead lead) {
        String repName = lead.getAssignedToUserId() == null ? "Unassigned" :
                userRepository.findById(lead.getAssignedToUserId()).map(User::getFullName).orElse("Unknown");
        boolean overdue = lead.getNextFollowUpStatus() == FollowUpStatus.PENDING
                && lead.getNextFollowUpDate() != null
                && lead.getNextFollowUpDate().isBefore(LocalDate.now());
        return new LeadListItemView(lead.getId(), lead.getLeadName(), lead.getCompanyName(), lead.getPhone(), lead.getEmail(),
                lead.getLeadSource(), repName, lead.getCurrentStage(), lead.getNextFollowUpDate(), lead.getNextFollowUpStatus(),
                lead.getLastActivityAt(), lead.getUpdatedAt(), lead.isArchived(), overdue);
    }

    private StageUpdateForm defaultStageForm(Lead lead) {
        StageUpdateForm form = new StageUpdateForm();
        form.setNewStage(lead.getCurrentStage());
        return form;
    }

    private ActivityForm defaultActivityForm() {
        ActivityForm form = new ActivityForm();
        form.setActivityDate(LocalDate.now());
        return form;
    }

    private FollowUpForm defaultFollowUpForm() {
        FollowUpForm form = new FollowUpForm();
        form.setDueDate(LocalDate.now().plusDays(1));
        return form;
    }
}
