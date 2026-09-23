package com.mustafizur.cleanarchitecture.infrastructure.config;

import com.mustafizur.cleanarchitecture.application.customer.handler.ChangeCustomerEmailHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.CreateCustomerHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.DeactivateCustomerHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.DeleteCustomerHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.GetCustomerByIdHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.ListCustomersHandler;
import com.mustafizur.cleanarchitecture.application.diagnostics.PingExternalServiceHandler;
import com.mustafizur.cleanarchitecture.application.messaging.CommandHandler;
import com.mustafizur.cleanarchitecture.application.messaging.Mediator;
import com.mustafizur.cleanarchitecture.application.messaging.QueryHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.application.port.out.DomainEventPublisher;
import com.mustafizur.cleanarchitecture.application.port.out.ExternalServicePort;
import com.mustafizur.cleanarchitecture.infrastructure.messaging.SpringMediator;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class InfrastructureConfiguration {

    @Bean
    RestClient.Builder restClientBuilder() {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder().requestFactory(requestFactory);
    }

    @Bean
    CreateCustomerHandler createCustomerHandler(CustomerRepository repository, DomainEventPublisher publisher) {
        return new CreateCustomerHandler(repository, publisher);
    }

    @Bean
    ChangeCustomerEmailHandler changeCustomerEmailHandler(CustomerRepository repository, DomainEventPublisher publisher) {
        return new ChangeCustomerEmailHandler(repository, publisher);
    }

    @Bean
    DeactivateCustomerHandler deactivateCustomerHandler(CustomerRepository repository, DomainEventPublisher publisher) {
        return new DeactivateCustomerHandler(repository, publisher);
    }

    @Bean
    DeleteCustomerHandler deleteCustomerHandler(CustomerRepository repository) {
        return new DeleteCustomerHandler(repository);
    }

    @Bean
    GetCustomerByIdHandler getCustomerByIdHandler(CustomerRepository repository) {
        return new GetCustomerByIdHandler(repository);
    }

    @Bean
    ListCustomersHandler listCustomersHandler(CustomerRepository repository) {
        return new ListCustomersHandler(repository);
    }

    @Bean
    PingExternalServiceHandler pingExternalServiceHandler(ExternalServicePort externalServicePort) {
        return new PingExternalServiceHandler(externalServicePort);
    }

    @Bean
    Mediator mediator(
            List<CommandHandler<?, ?>> commandHandlers,
            List<QueryHandler<?, ?>> queryHandlers,
            Validator validator,
            PlatformTransactionManager transactionManager) {
        return new SpringMediator(commandHandlers, queryHandlers, validator, transactionManager);
    }
}
