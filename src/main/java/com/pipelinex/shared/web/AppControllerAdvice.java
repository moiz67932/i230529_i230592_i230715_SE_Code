package com.pipelinex.shared.web;

import com.pipelinex.shared.security.CurrentUserAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AppControllerAdvice {

    private final CurrentUserAccessor currentUserAccessor;
    private final DateTimeFormatterView dateTimeFormatterView;

    public AppControllerAdvice(CurrentUserAccessor currentUserAccessor, DateTimeFormatterView dateTimeFormatterView) {
        this.currentUserAccessor = currentUserAccessor;
        this.dateTimeFormatterView = dateTimeFormatterView;
    }

    @ModelAttribute("currentUser")
    public CurrentUserView currentUser() {
        return currentUserAccessor.currentAuthUser()
                .map(user -> new CurrentUserView(user.getId(), user.getFullName(), user.getUsername(), user.getRole(), user.isAdmin(), user.isMustChangePassword()))
                .orElse(null);
    }

    @ModelAttribute("dates")
    public DateTimeFormatterView dates() {
        return dateTimeFormatterView;
    }
}
