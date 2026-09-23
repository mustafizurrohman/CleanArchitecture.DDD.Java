package com.mustafizur.cleanarchitecture.infrastructure.jobs;

import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerCreated;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CustomerCreatedJobListener {
    private final ObjectProvider<JobScheduler> jobSchedulerProvider;
    private final WelcomeCustomerJob welcomeCustomerJob;

    public CustomerCreatedJobListener(
            ObjectProvider<JobScheduler> jobSchedulerProvider,
            WelcomeCustomerJob welcomeCustomerJob) {
        this.jobSchedulerProvider = jobSchedulerProvider;
        this.welcomeCustomerJob = welcomeCustomerJob;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CustomerCreated event) {
        var jobScheduler = jobSchedulerProvider.getIfAvailable();
        if (jobScheduler == null) {
            return;
        }

        var customerId = event.customerId().toString();
        var email = event.email();
        jobScheduler.enqueue(() -> welcomeCustomerJob.execute(customerId, email));
    }
}
