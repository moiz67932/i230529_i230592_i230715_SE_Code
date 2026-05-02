package com.pipelinex.assignments;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadAssignmentHistoryRepository extends JpaRepository<LeadAssignmentHistory, Long> {

    List<LeadAssignmentHistory> findByLeadId(Long leadId, Sort sort);
}
