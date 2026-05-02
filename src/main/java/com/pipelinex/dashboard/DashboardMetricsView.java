package com.pipelinex.dashboard;

import java.util.Map;

public record DashboardMetricsView(long totalLeads,
                                   long unassignedLeads,
                                   long overdueFollowUps,
                                   long recentActivities,
                                   Map<String, Long> stageDistribution,
                                   Map<String, Long> sourceBreakdown,
                                   long assignedLeads) {
}
