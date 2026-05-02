package com.pipelinex.followups;

import com.pipelinex.shared.security.CurrentUserAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("isAuthenticated()")
public class FollowUpController {

    private final FollowUpService followUpService;
    private final CurrentUserAccessor currentUserAccessor;

    public FollowUpController(FollowUpService followUpService, CurrentUserAccessor currentUserAccessor) {
        this.followUpService = followUpService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping("/follow-ups")
    public String followUps(Model model) {
        model.addAttribute("items", followUpService.overdueForCurrentUser());
        model.addAttribute("overdueCount", followUpService.overdueCountForCurrentUser());
        model.addAttribute("title", "Follow-ups");
        model.addAttribute("activeNav", "followups");
        model.addAttribute("pageTitle", currentUserAccessor.requireAuthUser().isAdmin() ? "Overdue follow-ups" : "My overdue follow-ups");
        model.addAttribute("pageSubtitle", "Prioritize recovery work sorted by the oldest due items first.");
        return "followups/list";
    }
}
