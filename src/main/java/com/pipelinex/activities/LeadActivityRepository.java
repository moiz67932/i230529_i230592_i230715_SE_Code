package com.pipelinex.activities;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {

    List<LeadActivity> findByLeadId(Long leadId, Sort sort);

    @Query("""
        select count(a) from LeadActivity a
        where a.createdAt >= :since
        """)
    long countRecent(java.time.Instant since);

    long countByLeadId(Long leadId);

    long countByLoggedByAndActivityDateGreaterThanEqual(Long loggedBy, LocalDate since);
}
