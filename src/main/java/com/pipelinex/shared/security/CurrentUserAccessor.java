package com.pipelinex.shared.security;

import com.pipelinex.shared.error.NotFoundException;
import com.pipelinex.users.User;
import com.pipelinex.users.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserAccessor {

    private final UserRepository userRepository;

    public CurrentUserAccessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<AuthUser> currentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUser authUser) {
            return Optional.of(authUser);
        }
        return Optional.empty();
    }

    public AuthUser requireAuthUser() {
        return currentAuthUser().orElseThrow(() -> new NotFoundException("No authenticated user found."));
    }

    public User requireUser() {
        Long userId = requireAuthUser().getId();
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Authenticated user could not be found."));
    }
}
