package com.pipelinex.pipeline;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadStageHistoryRepository extends JpaRepository<LeadStageHistory, Long> {

    List<LeadStageHistory> findByLeadId(Long leadId, Sort sort);
}
