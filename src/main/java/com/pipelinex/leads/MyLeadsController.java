package com.pipelinex.leads;

import com.pipelinex.activities.ActivityForm;
import com.pipelinex.activities.ActivityService;
import com.pipelinex.followups.FollowUpForm;
import com.pipelinex.followups.FollowUpService;
import com.pipelinex.pipeline.PipelineService;
import com.pipelinex.pipeline.StageUpdateForm;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('REP')")
public class MyLeadsController {

    private final LeadService leadService;
    private final PipelineService pipelineService;
    private final ActivityService activityService;
    private final FollowUpService followUpService;

    public MyLeadsController(LeadService leadService,
                             PipelineService pipelineService,
                             ActivityService activityService,
                             FollowUpService followUpService) {
        this.leadService = leadService;
        this.pipelineService = pipelineService;
        this.activityService = activityService;
        this.followUpService = followUpService;
    }

    @GetMapping("/my-leads")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) LeadStage stage,
                       @RequestParam(required = false) FollowUpStatus followUpStatus,
                       @RequestParam(defaultValue = "false") boolean overdueOnly,
                       Model model) {
        model.addAttribute("leads", leadService.searchMyLeads(q, stage, followUpStatus, overdueOnly));
        model.addAttribute("title", "My Leads");
        model.addAttribute("activeNav", "my-leads");
        model.addAttribute("pageTitle", "My leads");
        model.addAttribute("pageSubtitle", "Focus on your assigned outreach with next actions and overdue risk in view.");
        return "rep/my-leads/list";
    }

    @GetMapping("/my-leads/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("leadDetail", leadService.repWorkspace(id));
        model.addAttribute("title", "My Lead");
        model.addAttribute("activeNav", "my-leads");
        model.addAttribute("pageTitle", "Lead workspace");
        model.addAttribute("pageSubtitle", "Work the lead with disciplined stage changes, activity logging, and follow-up control.");
        return "rep/my-leads/detail";
    }

    @PostMapping("/my-leads/{id}/stage")
    public String updateStage(@PathVariable Long id, @Valid @ModelAttribute StageUpdateForm stageUpdateForm, RedirectAttributes redirectAttributes) {
        pipelineService.updateStage(id, stageUpdateForm.getNewStage());
        redirectAttributes.addFlashAttribute("successMessage", "Lead stage updated.");
        return "redirect:/my-leads/" + id;
    }

    @PostMapping("/my-leads/{id}/activities")
    public String addActivity(@PathVariable Long id, @Valid @ModelAttribute ActivityForm activityForm, RedirectAttributes redirectAttributes) {
        activityService.logActivity(id, activityForm);
        redirectAttributes.addFlashAttribute("successMessage", "Activity logged.");
        return "redirect:/my-leads/" + id;
    }

    @PostMapping("/my-leads/{id}/follow-ups")
    public String scheduleFollowUp(@PathVariable Long id, @Valid @ModelAttribute FollowUpForm followUpForm, RedirectAttributes redirectAttributes) {
        followUpService.schedule(id, followUpForm);
        redirectAttributes.addFlashAttribute("successMessage", "Follow-up scheduled.");
        return "redirect:/my-leads/" + id;
    }

    @PostMapping("/my-follow-ups/{id}/complete")
    public String completeFollowUp(@PathVariable Long id,
                                   @RequestParam Long leadId,
                                   @RequestParam(required = false) String note,
                                   RedirectAttributes redirectAttributes) {
        followUpService.complete(id, note);
        redirectAttributes.addFlashAttribute("successMessage", "Follow-up completed.");
        return "redirect:/my-leads/" + leadId;
    }
}
