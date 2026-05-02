package com.pipelinex.shared.web;

import com.pipelinex.shared.error.BusinessRuleException;
import com.pipelinex.shared.error.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("title", "Not Found");
        model.addAttribute("message", ex.getMessage());
        return "shared/error";
    }

    @ExceptionHandler({BusinessRuleException.class, IllegalArgumentException.class})
    public String handleBusiness(Exception ex, Model model) {
        model.addAttribute("title", "Action could not be completed");
        model.addAttribute("message", ex.getMessage());
        return "shared/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("title", "Access denied");
        model.addAttribute("message", "You do not have permission to access this page.");
        return "shared/error";
    }
}
