package com.pipelinex.profile;

import com.pipelinex.shared.security.CurrentUserAccessor;
import com.pipelinex.users.UserProfileForm;
import com.pipelinex.users.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final CurrentUserAccessor currentUserAccessor;
    private final UserService userService;

    public ProfileController(CurrentUserAccessor currentUserAccessor, UserService userService) {
        this.currentUserAccessor = currentUserAccessor;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        var user = currentUserAccessor.requireUser();
        if (!model.containsAttribute("profileForm")) {
            UserProfileForm form = new UserProfileForm();
            form.setFullName(user.getFullName());
            form.setEmail(user.getEmail());
            model.addAttribute("profileForm", form);
        }
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordChangeForm());
        }
        model.addAttribute("title", "Profile");
        model.addAttribute("activeNav", "profile");
        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("pageSubtitle", "Update your account details and security settings.");
        return "profile/view";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") UserProfileForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordForm", new PasswordChangeForm());
            model.addAttribute("title", "Profile");
            model.addAttribute("activeNav", "profile");
            model.addAttribute("pageTitle", "Profile");
            model.addAttribute("pageSubtitle", "Update your account details and security settings.");
            return "profile/view";
        }
        userService.updateProfile(user.getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireUser();
        if (bindingResult.hasErrors()) {
            UserProfileForm profileForm = new UserProfileForm();
            profileForm.setFullName(user.getFullName());
            profileForm.setEmail(user.getEmail());
            model.addAttribute("profileForm", profileForm);
            model.addAttribute("title", "Profile");
            model.addAttribute("activeNav", "profile");
            model.addAttribute("pageTitle", "Profile");
            model.addAttribute("pageSubtitle", "Update your account details and security settings.");
            return "profile/view";
        }
        userService.changePassword(user.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmPassword(), false);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/profile/password/force")
    public String forcePasswordPage(Model model) {
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordChangeForm());
        }
        model.addAttribute("title", "Change Password");
        return "profile/force-password";
    }

    @PostMapping("/profile/password/force")
    public String forcePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireAuthUser();
        if (bindingResult.hasErrors()) {
            return "profile/force-password";
        }
        userService.changePassword(user.getId(), null, form.getNewPassword(), form.getConfirmPassword(), true);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated. Welcome back.");
        return "redirect:" + (user.isAdmin() ? "/dashboard" : "/my-leads");
    }
}
