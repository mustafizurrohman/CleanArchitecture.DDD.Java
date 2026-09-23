package com.mustafizur.cleanarchitecture.api.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "jobrunr.background-job-server.enabled=false",
        "jobrunr.dashboard.enabled=false",
        "jobrunr.job-scheduler.enabled=false"
})
@AutoConfigureMockMvc
class CustomerApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void customerLifecycleStartsWithCreateAndRead() throws Exception {
        var createResult = mvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada.integration@example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name", is("Ada Lovelace")))
                .andExpect(jsonPath("$.email", is("ada.integration@example.com")))
                .andReturn();

        var location = createResult.getResponse().getHeader("Location");
        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("ada.integration@example.com")));
    }

    @Test
    void invalidBodyUsesProblemDetails() throws Exception {
        mvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation failed")))
                .andExpect(jsonPath("$.errors.email").exists());
    }
}
