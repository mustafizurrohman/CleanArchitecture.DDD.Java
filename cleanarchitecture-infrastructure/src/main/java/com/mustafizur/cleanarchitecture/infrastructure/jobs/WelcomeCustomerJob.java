package com.mustafizur.cleanarchitecture.infrastructure.jobs;

import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WelcomeCustomerJob {
    private static final Logger log = LoggerFactory.getLogger(WelcomeCustomerJob.class);

    @Job(name = "Welcome newly created customer")
    public void execute(String customerId, String email) {
        // Replace with a real mail adapter in production.
        log.info("Background welcome job completed customerId={} email={}", customerId, email);
    }
}
