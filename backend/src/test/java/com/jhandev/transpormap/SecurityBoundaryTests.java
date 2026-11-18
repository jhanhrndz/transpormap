package com.jhandev.transpormap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityBoundaryTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void healthIsPublicAndShowsOnlyBasicStatus() throws Exception {
        mvc.perform(get("/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"up\"}"));
    }

    @Test
    void anonymousCannotReadAlerts() throws Exception {
        mvc.perform(get("/v1/alerts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void authenticatedUserCannotAccessUnimplementedAlerts() throws Exception {
        mvc.perform(get("/v1/alerts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void futureNonGetHealthOperationsAreNotPublic() throws Exception {
        mvc.perform(post("/v1/health").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void actuatorHealthIsNotPublic() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isUnauthorized());
    }
}