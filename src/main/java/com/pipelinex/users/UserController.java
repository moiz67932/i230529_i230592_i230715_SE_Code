package com.pipelinex.users;

import com.pipelinex.shared.domain.UserStatus;
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
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) UserStatus status,
                       @RequestParam(defaultValue = "name") String sort,
                       Model model) {
        model.addAttribute("users", userService.searchReps(q, status, sort));
        model.addAttribute("query", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("sort", sort);
        model.addAttribute("title", "Users");
        model.addAttribute("activeNav", "users");
        model.addAttribute("pageTitle", "User management");
        model.addAttribute("pageSubtitle", "Onboard reps, manage access, and monitor active workload.");
        return "admin/users/list";
    }

    @GetMapping("/users/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserForm());
        }
        model.addAttribute("title", "Create Rep");
        model.addAttribute("activeNav", "users");
        model.addAttribute("pageTitle", "Create rep");
        model.addAttribute("pageSubtitle", "Provision a rep account with a temporary password and secure first login.");
        return "admin/users/form";
    }

    @PostMapping("/users")
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Create Rep");
            model.addAttribute("activeNav", "users");
            model.addAttribute("pageTitle", "Create rep");
            model.addAttribute("pageSubtitle", "Provision a rep account with a temporary password and secure first login.");
            return "admin/users/form";
        }
        User user = userService.createRep(form);
        redirectAttributes.addFlashAttribute("successMessage", "Rep created successfully.");
        return "redirect:/users/" + user.getId();
    }

    @GetMapping("/users/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("userDetail", userService.getDetail(id));
        model.addAttribute("title", "User Detail");
        model.addAttribute("activeNav", "users");
        model.addAttribute("pageTitle", "Rep detail");
        model.addAttribute("pageSubtitle", "Access status, workload, and activity summary for this rep.");
        return "admin/users/detail";
    }

    @PostMapping("/users/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam UserStatus status,
                               RedirectAttributes redirectAttributes) {
        userService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated.");
        return "redirect:/users/" + id;
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String temporaryPassword,
                                RedirectAttributes redirectAttributes) {
        userService.resetPassword(id, temporaryPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Temporary password reset successfully.");
        return "redirect:/users/" + id;
    }
}
