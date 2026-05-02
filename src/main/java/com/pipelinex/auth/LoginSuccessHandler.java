package com.pipelinex.auth;

import com.pipelinex.shared.security.AuthUser;
import com.pipelinex.users.User;
import com.pipelinex.users.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        User user = userRepository.findById(authUser.getId()).orElseThrow();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        if (authUser.isMustChangePassword()) {
            response.sendRedirect("/profile/password/force");
            return;
        }

        if (authUser.isAdmin()) {
            response.sendRedirect("/dashboard");
            return;
        }
        response.sendRedirect("/my-leads");
    }
}
