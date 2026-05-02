package com.pipelinex.pipeline;

import com.pipelinex.followups.LeadFollowUpRepository;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import com.pipelinex.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PipelineServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    PipelineService pipelineService;

    @Autowired
    LeadFollowUpRepository followUpRepository;

    @Test
    @WithUserDetails("admin@pipelinex.local")
    void terminalStageAutoClosesPendingFollowUps() {
        pipelineService.updateStage(2L, LeadStage.LOST);

        assertThat(followUpRepository.findByLeadId(2L, org.springframework.data.domain.Sort.by("id")))
                .extracting(item -> item.getStatus().name())
                .contains("CANCELLED");
        assertThat(followUpRepository.findByLeadId(2L, org.springframework.data.domain.Sort.by("id")).getFirst().getStatus())
                .isEqualTo(FollowUpStatus.CANCELLED);
    }
}
