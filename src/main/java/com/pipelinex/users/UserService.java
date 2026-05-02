package com.pipelinex.users;

import com.pipelinex.activities.LeadActivityRepository;
import com.pipelinex.followups.LeadFollowUpRepository;
import com.pipelinex.leads.LeadRepository;
import com.pipelinex.shared.domain.FollowUpStatus;
import com.pipelinex.shared.domain.Role;
import com.pipelinex.shared.domain.UserStatus;
import com.pipelinex.shared.error.BusinessRuleException;
import com.pipelinex.shared.error.NotFoundException;
import com.pipelinex.shared.security.CurrentUserAccessor;
import com.pipelinex.shared.util.NormalizationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final LeadFollowUpRepository leadFollowUpRepository;
    private final LeadActivityRepository leadActivityRepository;
    private final CurrentUserAccessor currentUserAccessor;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       LeadRepository leadRepository,
                       LeadFollowUpRepository leadFollowUpRepository,
                       LeadActivityRepository leadActivityRepository,
                       CurrentUserAccessor currentUserAccessor,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.leadFollowUpRepository = leadFollowUpRepository;
        this.leadActivityRepository = leadActivityRepository;
        this.currentUserAccessor = currentUserAccessor;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserListItemView> searchReps(String query, UserStatus status, String sortKey) {
        List<User> users = userRepository.searchReps(NormalizationUtils.trimToNull(query), status, Sort.by("fullName").ascending());
        Comparator<UserListItemView> comparator = switch (sortKey == null ? "name" : sortKey) {
            case "created" -> Comparator.comparing(UserListItemView::createdAt).reversed();
            case "workload" -> Comparator.comparingLong(UserListItemView::workload).reversed();
            default -> Comparator.comparing(UserListItemView::fullName, String.CASE_INSENSITIVE_ORDER);
        };
        return users.stream().map(this::toListItem).sorted(comparator).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserOptionView> activeRepOptions() {
        return userRepository.findByRoleAndStatus(Role.REP, UserStatus.ACTIVE, Sort.by("fullName").ascending())
                .stream()
                .map(user -> new UserOptionView(user.getId(), user.getFullName()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public User createRep(UserForm form) {
        String email = NormalizationUtils.normalizeEmail(form.getEmail());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("A user with that email already exists.");
        }
        User actor = currentUserAccessor.requireUser();
        User user = new User();
        user.setFullName(NormalizationUtils.trimToNull(form.getFullName()));
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(form.getTemporaryPassword()));
        user.setRole(Role.REP);
        user.setStatus(UserStatus.ACTIVE);
        user.setMustChangePassword(true);
        user.setCreatedBy(actor.getId());
        user.setUpdatedBy(actor.getId());
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailView getDetail(Long id) {
        return toDetail(getRep(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        User actor = currentUserAccessor.requireUser();
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found."));
        if (user.getRole() == Role.ADMIN && user.getId().equals(actor.getId()) && status == UserStatus.INACTIVE
                && userRepository.countByRoleAndStatus(Role.ADMIN, UserStatus.ACTIVE) <= 1) {
            throw new BusinessRuleException("You cannot deactivate the only active admin account.");
        }
        user.setStatus(status);
        user.setUpdatedBy(actor.getId());
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void resetPassword(Long id, String temporaryPassword) {
        User actor = currentUserAccessor.requireUser();
        User user = getRep(id);
        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setUpdatedBy(actor.getId());
        userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found."));
    }

    @Transactional
    public void updateProfile(Long userId, UserProfileForm form) {
        User user = getById(userId);
        String email = NormalizationUtils.normalizeEmail(form.getEmail());
        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Another account already uses that email.");
                });
        user.setFullName(NormalizationUtils.trimToNull(form.getFullName()));
        user.setEmail(email);
        user.setUpdatedBy(userId);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword, boolean forced) {
        User user = getById(userId);
        if (!forced && (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPasswordHash()))) {
            throw new BusinessRuleException("Current password is incorrect.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessRuleException("New password and confirmation must match.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        user.setUpdatedBy(userId);
        userRepository.save(user);
    }

    private UserListItemView toListItem(User user) {
        long workload = leadRepository.countByAssignedToUserIdAndArchivedFalse(user.getId());
        return new UserListItemView(user.getId(), user.getFullName(), user.getEmail(), user.getStatus(), workload, user.getCreatedAt(), user.isMustChangePassword());
    }

    private UserDetailView toDetail(User user) {
        return new UserDetailView(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt(),
                leadRepository.countByAssignedToUserIdAndArchivedFalse(user.getId()),
                leadFollowUpRepository.countByAssignedRepIdAndStatusAndDueDateBefore(user.getId(), FollowUpStatus.PENDING, LocalDate.now()),
                leadActivityRepository.countByLoggedByAndActivityDateGreaterThanEqual(user.getId(), LocalDate.now().minusDays(7)),
                user.isMustChangePassword()
        );
    }

    private User getRep(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found."));
        if (user.getRole() != Role.REP) {
            throw new BusinessRuleException("Only rep users can be managed here.");
        }
        return user;
    }
}
