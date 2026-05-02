package com.pipelinex.analytics;

import com.pipelinex.dashboard.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final DashboardService dashboardService;

    public AnalyticsController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("metrics", dashboardService.adminMetrics());
        model.addAttribute("title", "Analytics");
        model.addAttribute("activeNav", "analytics");
        model.addAttribute("pageTitle", "Analytics");
        model.addAttribute("pageSubtitle", "Stage distribution, source mix, and team performance signals.");
        return "analytics/admin";
    }
}
