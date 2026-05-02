package com.pipelinex.leads;

import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    @Query("""
        select l from Lead l
        where (:query is null or lower(l.leadName) like :query
             or lower(l.companyName) like :query
             or lower(coalesce(l.phone, '')) like :query
             or lower(coalesce(l.email, '')) like :query)
          and (:stage is null or l.currentStage = :stage)
          and (:assignedRepId is null or l.assignedToUserId = :assignedRepId)
          and (:assignmentState is null or (:assignmentState = 'assigned' and l.assignedToUserId is not null) or (:assignmentState = 'unassigned' and l.assignedToUserId is null))
          and (:followUpStatus is null or l.nextFollowUpStatus = :followUpStatus)
          and (:archived is null or l.archived = :archived)
          and (:leadSource is null or lower(l.leadSource) = :leadSource)
        """)
    List<Lead> searchAdmin(String query,
                           LeadStage stage,
                           Long assignedRepId,
                           String assignmentState,
                           FollowUpStatus followUpStatus,
                           Boolean archived,
                           String leadSource,
                           Sort sort);

    @Query("""
        select l from Lead l
        where l.assignedToUserId = :repId
          and (:query is null or lower(l.leadName) like :query
             or lower(l.companyName) like :query
             or lower(coalesce(l.phone, '')) like :query
             or lower(coalesce(l.email, '')) like :query)
          and (:stage is null or l.currentStage = :stage)
          and (:followUpStatus is null or l.nextFollowUpStatus = :followUpStatus)
          and (:overdueOnly = false or (l.nextFollowUpStatus = com.pipelinex.shared.domain.FollowUpStatus.PENDING and l.nextFollowUpDate < current_date))
          and (:archived is null or l.archived = :archived)
        """)
    List<Lead> searchRep(Long repId,
                         String query,
                         LeadStage stage,
                         FollowUpStatus followUpStatus,
                         boolean overdueOnly,
                         Boolean archived,
                         Sort sort);

    @Query("""
        select l from Lead l
        where (lower(coalesce(l.phone, '')) = lower(:phone) and :phone is not null)
           or (lower(coalesce(l.email, '')) = lower(:email) and :email is not null)
        """)
    List<Lead> findDuplicates(String phone, String email);

    Optional<Lead> findByIdAndAssignedToUserId(Long id, Long repId);

    long countByArchivedFalse();

    long countByArchivedFalseAndAssignedToUserIdIsNull();

    long countByAssignedToUserIdAndArchivedFalse(Long repId);

    List<Lead> findByArchivedFalse(Sort sort);

    List<Lead> findByAssignedToUserIdAndArchivedFalse(Long repId, Sort sort);
}
