package com.pipelinex.auth;

import com.pipelinex.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void adminLoginRedirectsToDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").user("email", "admin@pipelinex.local").password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void repLoginRedirectsToForcedPasswordChange() throws Exception {
        mockMvc.perform(formLogin("/login").user("email", "rep.noah@pipelinex.local").password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/password/force"));
    }

    @Test
    @WithUserDetails("rep.noah@pipelinex.local")
    void repCannotAccessAdminUsersPage() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("rep.noah@pipelinex.local")
    void repCannotAccessAnotherRepsLead() throws Exception {
        mockMvc.perform(get("/my-leads/2"))
                .andExpect(status().isForbidden());
    }
}
