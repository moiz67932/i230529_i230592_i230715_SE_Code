package com.pipelinex.leads;

import com.pipelinex.activities.ActivityForm;
import com.pipelinex.activities.ActivityService;
import com.pipelinex.assignments.AssignmentForm;
import com.pipelinex.followups.FollowUpForm;
import com.pipelinex.followups.FollowUpService;
import com.pipelinex.pipeline.PipelineService;
import com.pipelinex.pipeline.StageUpdateForm;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.LeadStage;
import com.pipelinex.users.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class LeadController {

    private final LeadService leadService;
    private final UserService userService;
    private final PipelineService pipelineService;
    private final ActivityService activityService;
    private final FollowUpService followUpService;

    public LeadController(LeadService leadService,
                          UserService userService,
                          PipelineService pipelineService,
                          ActivityService activityService,
                          FollowUpService followUpService) {
        this.leadService = leadService;
        this.userService = userService;
        this.pipelineService = pipelineService;
        this.activityService = activityService;
        this.followUpService = followUpService;
    }

    @GetMapping("/leads")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) LeadStage stage,
                       @RequestParam(required = false) Long assignedRepId,
                       @RequestParam(required = false) String assignmentState,
                       @RequestParam(required = false) FollowUpStatus followUpStatus,
                       @RequestParam(required = false, defaultValue = "false") Boolean archived,
                       @RequestParam(required = false) String leadSource,
                       Model model) {
        model.addAttribute("leads", leadService.searchAdmin(q, stage, assignedRepId, assignmentState, followUpStatus, archived, leadSource));
        model.addAttribute("repOptions", userService.activeRepOptions());
        model.addAttribute("title", "Leads");
        model.addAttribute("activeNav", "leads");
        model.addAttribute("pageTitle", "Lead management");
        model.addAttribute("pageSubtitle", "Search, assign, and monitor every active prospect in the pipeline.");
        return "admin/leads/list";
    }

    @GetMapping("/leads/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("leadForm")) {
            model.addAttribute("leadForm", new LeadForm());
        }
        model.addAttribute("repOptions", userService.activeRepOptions());
        model.addAttribute("title", "Create Lead");
        model.addAttribute("activeNav", "leads");
        model.addAttribute("pageTitle", "Create lead");
        model.addAttribute("pageSubtitle", "Capture a new opportunity and optionally assign ownership immediately.");
        return "admin/leads/form";
    }

    @PostMapping("/leads")
    public String create(@Valid @ModelAttribute("leadForm") LeadForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("repOptions", userService.activeRepOptions());
            model.addAttribute("title", "Create Lead");
            model.addAttribute("activeNav", "leads");
            model.addAttribute("pageTitle", "Create lead");
            model.addAttribute("pageSubtitle", "Capture a new opportunity and optionally assign ownership immediately.");
            return "admin/leads/form";
        }
        Lead lead = leadService.createLead(form);
        redirectAttributes.addFlashAttribute("successMessage", "Lead created successfully.");
        return "redirect:/leads/" + lead.getId();
    }

    @GetMapping("/leads/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("leadDetail", leadService.adminWorkspace(id, userService.activeRepOptions()));
        model.addAttribute("title", "Lead Detail");
        model.addAttribute("activeNav", "leads");
        model.addAttribute("pageTitle", "Lead workspace");
        model.addAttribute("pageSubtitle", "Operational history, assignment, stage, activity, and follow-up control in one place.");
        return "admin/leads/detail";
    }

    @GetMapping("/leads/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Lead lead = leadService.requireLead(id);
        LeadForm form = new LeadForm();
        form.setLeadName(lead.getLeadName());
        form.setPhone(lead.getPhone());
        form.setEmail(lead.getEmail());
        form.setCompanyName(lead.getCompanyName());
        form.setLeadSource(lead.getLeadSource());
        form.setCompanyWebsite(lead.getCompanyWebsite());
        form.setJobTitle(lead.getJobTitle());
        form.setLinkedinUrl(lead.getLinkedinUrl());
        form.setNotes(lead.getNotes());
        model.addAttribute("leadId", id);
        model.addAttribute("leadForm", form);
        model.addAttribute("repOptions", userService.activeRepOptions());
        model.addAttribute("title", "Edit Lead");
        model.addAttribute("activeNav", "leads");
        model.addAttribute("pageTitle", "Edit lead");
        model.addAttribute("pageSubtitle", "Update lead profile information without changing assignment history.");
        return "admin/leads/form";
    }

    @PostMapping("/leads/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("leadForm") LeadForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("leadId", id);
            model.addAttribute("repOptions", userService.activeRepOptions());
            model.addAttribute("title", "Edit Lead");
            model.addAttribute("activeNav", "leads");
            model.addAttribute("pageTitle", "Edit lead");
            model.addAttribute("pageSubtitle", "Update lead profile information without changing assignment history.");
            return "admin/leads/form";
        }
        leadService.updateLead(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Lead updated successfully.");
        return "redirect:/leads/" + id;
    }

    @PostMapping("/leads/{id}/archive")
    public String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        leadService.archive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Lead archived successfully.");
        return "redirect:/leads";
    }

    @PostMapping("/leads/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        leadService.restore(id);
        redirectAttributes.addFlashAttribute("successMessage", "Lead restored successfully.");
        return "redirect:/leads";
    }

    @PostMapping("/leads/{id}/assign")
    public String assign(@PathVariable Long id, @Valid @ModelAttribute AssignmentForm assignmentForm, RedirectAttributes redirectAttributes) {
        leadService.assignLead(id, assignmentForm);
        redirectAttributes.addFlashAttribute("successMessage", "Lead assignment updated.");
        return "redirect:/leads/" + id;
    }

    @PostMapping("/leads/{id}/stage")
    public String updateStage(@PathVariable Long id, @Valid @ModelAttribute StageUpdateForm stageUpdateForm, RedirectAttributes redirectAttributes) {
        pipelineService.updateStage(id, stageUpdateForm.getNewStage());
        redirectAttributes.addFlashAttribute("successMessage", "Lead stage updated.");
        return "redirect:/leads/" + id;
    }

    @PostMapping("/leads/{id}/activities")
    public String addActivity(@PathVariable Long id, @Valid @ModelAttribute ActivityForm activityForm, RedirectAttributes redirectAttributes) {
        activityService.logActivity(id, activityForm);
        redirectAttributes.addFlashAttribute("successMessage", "Activity logged.");
        return "redirect:/leads/" + id;
    }

    @PostMapping("/leads/{id}/follow-ups")
    public String scheduleFollowUp(@PathVariable Long id, @Valid @ModelAttribute FollowUpForm followUpForm, RedirectAttributes redirectAttributes) {
        followUpService.schedule(id, followUpForm);
        redirectAttributes.addFlashAttribute("successMessage", "Follow-up scheduled.");
        return "redirect:/leads/" + id;
    }

    @PostMapping("/follow-ups/{id}/complete")
    public String completeFollowUp(@PathVariable Long id,
                                   @RequestParam Long leadId,
                                   @RequestParam(required = false) String note,
                                   RedirectAttributes redirectAttributes) {
        followUpService.complete(id, note);
        redirectAttributes.addFlashAttribute("successMessage", "Follow-up completed.");
        return "redirect:/leads/" + leadId;
    }

    @PostMapping("/follow-ups/{id}/reschedule")
    public String rescheduleFollowUp(@PathVariable Long id,
                                     @RequestParam Long leadId,
                                     @RequestParam java.time.LocalDate dueDate,
                                     RedirectAttributes redirectAttributes) {
        FollowUpForm form = new FollowUpForm();
        form.setDueDate(dueDate);
        followUpService.reschedule(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Follow-up rescheduled.");
        return "redirect:/leads/" + leadId;
    }
}
