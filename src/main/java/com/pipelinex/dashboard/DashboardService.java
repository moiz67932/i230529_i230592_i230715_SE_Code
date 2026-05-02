package com.pipelinex.dashboard;

import com.pipelinex.activities.LeadActivityRepository;
import com.pipelinex.followups.FollowUpService;
import com.pipelinex.leads.LeadRepository;
import com.pipelinex.shared.security.CurrentUserAccessor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final LeadActivityRepository activityRepository;
    private final FollowUpService followUpService;
    private final CurrentUserAccessor currentUserAccessor;

    public DashboardService(LeadRepository leadRepository,
                            LeadActivityRepository activityRepository,
                            FollowUpService followUpService,
                            CurrentUserAccessor currentUserAccessor) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.followUpService = followUpService;
        this.currentUserAccessor = currentUserAccessor;
    }

    public DashboardMetricsView adminMetrics() {
        Map<String, Long> stageDistribution = new LinkedHashMap<>();
        Arrays.stream(com.pipelinex.shared.domain.LeadStage.values()).forEach(stage ->
                stageDistribution.put(stage.name(), leadRepository.findByArchivedFalse(org.springframework.data.domain.Sort.unsorted())
                        .stream().filter(lead -> lead.getCurrentStage() == stage).count()));
        Map<String, Long> sourceBreakdown = new LinkedHashMap<>();
        leadRepository.findByArchivedFalse(org.springframework.data.domain.Sort.unsorted())
                .forEach(lead -> sourceBreakdown.merge(lead.getLeadSource(), 1L, Long::sum));
        return new DashboardMetricsView(
                leadRepository.countByArchivedFalse(),
                leadRepository.countByArchivedFalseAndAssignedToUserIdIsNull(),
                followUpService.overdueCountForCurrentUser(),
                activityRepository.countRecent(Instant.now().minusSeconds(7 * 24 * 60 * 60)),
                stageDistribution,
                sourceBreakdown,
                0
        );
    }

    public DashboardMetricsView repMetrics() {
        Long repId = currentUserAccessor.requireUser().getId();
        Map<String, Long> stageDistribution = new LinkedHashMap<>();
        Arrays.stream(com.pipelinex.shared.domain.LeadStage.values()).forEach(stage ->
                stageDistribution.put(stage.name(), leadRepository.findByAssignedToUserIdAndArchivedFalse(repId, org.springframework.data.domain.Sort.unsorted())
                        .stream().filter(lead -> lead.getCurrentStage() == stage).count()));
        return new DashboardMetricsView(
                leadRepository.countByAssignedToUserIdAndArchivedFalse(repId),
                0,
                followUpService.overdueCountForCurrentUser(),
                activityRepository.countByLoggedByAndActivityDateGreaterThanEqual(repId, java.time.LocalDate.now().minusDays(7)),
                stageDistribution,
                Map.of(),
                leadRepository.countByAssignedToUserIdAndArchivedFalse(repId)
        );
    }
}
