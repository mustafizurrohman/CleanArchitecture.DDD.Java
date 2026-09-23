package com.mustafizur.cleanarchitecture;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "jobrunr.background-job-server.enabled=false",
        "jobrunr.dashboard.enabled=false",
        "jobrunr.job-scheduler.enabled=false"
})
class CleanArchitectureApplicationTest {
    @Test
    void contextLoads() {
    }
}
