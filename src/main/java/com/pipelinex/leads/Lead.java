package com.pipelinex.leads;

import com.pipelinex.shared.domain.AuditableEntity;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "leads")
public class Lead extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_name", nullable = false, length = 150)
    private String leadName;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(length = 60)
    private String phone;

    @Column(length = 180)
    private String email;

    @Column(name = "lead_source", nullable = false, length = 120)
    private String leadSource;

    @Column(name = "company_website", length = 200)
    private String companyWebsite;

    @Column(name = "job_title", length = 120)
    private String jobTitle;

    @Column(name = "linkedin_url", length = 200)
    private String linkedinUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false, length = 30)
    private LeadStage currentStage;

    @Column(name = "assigned_to_user_id")
    private Long assignedToUserId;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(nullable = false)
    private boolean archived;

    @Column(name = "next_follow_up_date")
    private LocalDate nextFollowUpDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_follow_up_status", length = 20)
    private FollowUpStatus nextFollowUpStatus;

    @Column(name = "last_activity_at")
    private LocalDate lastActivityAt;

    public Long getId() {
        return id;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LeadStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(LeadStage currentStage) {
        this.currentStage = currentStage;
    }

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDate getNextFollowUpDate() {
        return nextFollowUpDate;
    }

    public void setNextFollowUpDate(LocalDate nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }

    public FollowUpStatus getNextFollowUpStatus() {
        return nextFollowUpStatus;
    }

    public void setNextFollowUpStatus(FollowUpStatus nextFollowUpStatus) {
        this.nextFollowUpStatus = nextFollowUpStatus;
    }

    public LocalDate getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDate lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (currentStage == null) {
            currentStage = LeadStage.NEW;
        }
        if (getCreatedAt() == null) {
            setCreatedAt(now);
        }
        if (getUpdatedAt() == null) {
            setUpdatedAt(now);
        }
    }

    @PreUpdate
    void onUpdate() {
        setUpdatedAt(Instant.now());
    }
}
