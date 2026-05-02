package com.pipelinex.users;

import com.pipelinex.shared.domain.Role;
import com.pipelinex.shared.domain.UserStatus;

import java.time.Instant;

public record UserDetailView(Long id,
                             String fullName,
                             String email,
                             Role role,
                             UserStatus status,
                             Instant createdAt,
                             Instant updatedAt,
                             Instant lastLoginAt,
                             long assignedLeads,
                             long overdueFollowUps,
                             long recentActivities,
                             boolean mustChangePassword) {
}
