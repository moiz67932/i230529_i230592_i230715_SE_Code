package com.pipelinex.followups;

import com.pipelinex.shared.domain.FollowUpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeadFollowUpRepository extends JpaRepository<LeadFollowUp, Long> {

    List<LeadFollowUp> findByLeadId(Long leadId, Sort sort);

    List<LeadFollowUp> findByLeadIdAndStatus(Long leadId, FollowUpStatus status, Sort sort);

    @Query("""
        select f from LeadFollowUp f
        where f.status = com.pipelinex.shared.domain.FollowUpStatus.PENDING
          and f.dueDate < current_date
          and (:repId is null or f.assignedRepId = :repId)
        """)
    List<LeadFollowUp> findOverdue(Long repId, Sort sort);

    @Query("""
        select count(f) from LeadFollowUp f
        where f.status = com.pipelinex.shared.domain.FollowUpStatus.PENDING
          and f.dueDate < current_date
          and (:repId is null or f.assignedRepId = :repId)
        """)
    long countOverdue(Long repId);

    @Query("""
        select f from LeadFollowUp f
        where f.status = com.pipelinex.shared.domain.FollowUpStatus.PENDING
          and f.assignedRepId = :repId
        """)
    List<LeadFollowUp> findPendingForRep(Long repId);

    @Query("""
        select f from LeadFollowUp f
        where f.leadId = :leadId and f.status = com.pipelinex.shared.domain.FollowUpStatus.PENDING
        order by f.dueDate asc
        """)
    List<LeadFollowUp> findPendingForLead(Long leadId);

    long countByAssignedRepIdAndStatus(Long assignedRepId, FollowUpStatus status);

    long countByAssignedRepIdAndStatusAndDueDateBefore(Long assignedRepId, FollowUpStatus status, LocalDate dueDate);
}
