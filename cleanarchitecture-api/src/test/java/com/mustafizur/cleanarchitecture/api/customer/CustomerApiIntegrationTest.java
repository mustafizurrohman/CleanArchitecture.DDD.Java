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

    @Test
    void notFoundCustomerReturnsProblemDetails() throws Exception {
        mvc.perform(get("/api/v1/customers/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource not found")));
    }

    @Test
    void duplicateCustomerReturnsConflictProblemDetails() throws Exception {
        mvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Grace Hopper","email":"grace.conflict@example.com"}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Grace Hopper Duplicate","email":"grace.conflict@example.com"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Conflict")));
    }

    @Test
    void malformedBodyReturnsBadRequestProblemDetails() throws Exception {
        mvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Malformed request")));
    }
}
