package com.pipelinex.shared.web;

import com.pipelinex.shared.domain.Role;

public record CurrentUserView(Long id, String fullName, String email, Role role, boolean admin, boolean mustChangePassword) {
}
