package com.pipelinex.auth;

import com.pipelinex.shared.security.AuthUser;
import com.pipelinex.users.User;
import com.pipelinex.users.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PipelineXUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public PipelineXUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
        return new AuthUser(user.getId(), user.getFullName(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.getStatus(), user.isMustChangePassword());
    }
}
