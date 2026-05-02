package com.pipelinex.users;

import com.pipelinex.shared.domain.UserStatus;

import java.time.Instant;

public record UserListItemView(Long id,
                               String fullName,
                               String email,
                               UserStatus status,
                               long workload,
                               Instant createdAt,
                               boolean mustChangePassword) {
}
