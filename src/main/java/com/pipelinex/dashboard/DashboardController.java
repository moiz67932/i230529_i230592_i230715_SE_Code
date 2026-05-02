package com.pipelinex.dashboard;

import com.pipelinex.shared.security.CurrentUserAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final CurrentUserAccessor currentUserAccessor;
    private final DashboardService dashboardService;

    public DashboardController(CurrentUserAccessor currentUserAccessor, DashboardService dashboardService) {
        this.currentUserAccessor = currentUserAccessor;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        boolean admin = currentUserAccessor.requireAuthUser().isAdmin();
        model.addAttribute("metrics", admin ? dashboardService.adminMetrics() : dashboardService.repMetrics());
        model.addAttribute("title", "Dashboard");
        model.addAttribute("activeNav", admin ? "dashboard" : "my-leads");
        model.addAttribute("pageTitle", admin ? "Operational dashboard" : "Personal workload");
        model.addAttribute("pageSubtitle", admin
                ? "Monitor team capacity, overdue risk, and pipeline health at a glance."
                : "See your assigned pipeline, overdue work, and recent execution trends.");
        return admin ? "dashboard/admin" : "dashboard/rep";
    }
}
