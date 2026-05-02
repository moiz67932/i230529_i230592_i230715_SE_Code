package com.pipelinex.leads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LeadForm {

    @NotBlank
    @Size(max = 150)
    private String leadName;

    @Size(max = 60)
    private String phone;

    @Size(max = 180)
    private String email;

    @NotBlank
    @Size(max = 150)
    private String companyName;

    @NotBlank
    @Size(max = 120)
    private String leadSource;

    @Size(max = 200)
    private String companyWebsite;

    @Size(max = 120)
    private String jobTitle;

    @Size(max = 200)
    private String linkedinUrl;

    private String notes;

    private Long assignedRepId;

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public Long getAssignedRepId() {
        return assignedRepId;
    }

    public void setAssignedRepId(Long assignedRepId) {
        this.assignedRepId = assignedRepId;
    }
}
