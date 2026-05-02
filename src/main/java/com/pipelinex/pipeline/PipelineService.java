package com.pipelinex.pipeline;

import com.pipelinex.followups.FollowUpService;
import com.pipelinex.leads.Lead;
import com.pipelinex.leads.LeadRepository;
import com.pipelinex.leads.LeadService;
import com.pipelinex.shared.domain.LeadStage;
import com.pipelinex.shared.security.CurrentUserAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PipelineService {

    private final LeadService leadService;
    private final LeadRepository leadRepository;
    private final LeadStageHistoryRepository stageHistoryRepository;
    private final CurrentUserAccessor currentUserAccessor;
    private final FollowUpService followUpService;

    public PipelineService(LeadService leadService,
                           LeadRepository leadRepository,
                           LeadStageHistoryRepository stageHistoryRepository,
                           CurrentUserAccessor currentUserAccessor,
                           FollowUpService followUpService) {
        this.leadService = leadService;
        this.leadRepository = leadRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.currentUserAccessor = currentUserAccessor;
        this.followUpService = followUpService;
    }

    public void updateStage(Long leadId, LeadStage newStage) {
        Lead lead = leadService.requireLeadForCurrentUser(leadId);
        if (lead.getCurrentStage() == newStage) {
            return;
        }
        LeadStage previousStage = lead.getCurrentStage();
        lead.setCurrentStage(newStage);
        lead.setUpdatedBy(currentUserAccessor.requireUser().getId());
        leadRepository.save(lead);

        LeadStageHistory history = new LeadStageHistory();
        history.setLeadId(leadId);
        history.setPreviousStage(previousStage);
        history.setNewStage(newStage);
        history.setChangedBy(currentUserAccessor.requireUser().getId());
        stageHistoryRepository.save(history);

        if (newStage.terminal()) {
            followUpService.closePendingForTerminalStage(lead, newStage);
        }
    }
}
