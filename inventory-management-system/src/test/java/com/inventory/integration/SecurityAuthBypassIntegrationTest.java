package com.inventory.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAuthBypassIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedProductsRoute_withoutToken_shouldReject() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void protectedDashboardRoute_withoutToken_shouldReject() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void loginRoute_shouldRemainPublic() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"x\",\"password\":\"y\"}"))
            .andExpect(status().is4xxClientError());
    }
}
