package com.pipelinex.auth;

import com.pipelinex.shared.security.CurrentUserAccessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ForcePasswordChangeInterceptor implements HandlerInterceptor {

    private final CurrentUserAccessor currentUserAccessor;

    public ForcePasswordChangeInterceptor(CurrentUserAccessor currentUserAccessor) {
        this.currentUserAccessor = currentUserAccessor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return currentUserAccessor.currentAuthUser()
                .map(user -> {
                    String path = request.getRequestURI();
                    boolean allowed = path.startsWith("/profile/password") || path.startsWith("/logout") || path.startsWith("/css")
                            || path.startsWith("/js") || path.startsWith("/webjars");
                    if (user.isMustChangePassword() && !allowed) {
                        try {
                            response.sendRedirect("/profile/password/force");
                        } catch (Exception ignored) {
                            return false;
                        }
                        return false;
                    }
                    return true;
                })
                .orElse(true);
    }
}
