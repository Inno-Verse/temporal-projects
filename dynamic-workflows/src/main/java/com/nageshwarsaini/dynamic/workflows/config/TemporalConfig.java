package com.nageshwarsaini.dynamic.workflows.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Temporal client and service stubs.
 *
 * @author nageshwarsaini
 */
@Configuration
public class TemporalConfig {

    /**
     * Creates and provides a {@link WorkflowServiceStubs} instance connecting to the local Temporal server.
     *
     * @return a configured {@link WorkflowServiceStubs} bean
     */
    @Bean(destroyMethod = "shutdown")
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget("localhost:7233").build()
        );
    }

    /**
     * Creates and provides a {@link WorkflowClient} instance for interacting with Temporal workflows.
     *
     * @param serviceStubs the {@link WorkflowServiceStubs} to use for communication
     * @return a configured {@link WorkflowClient} bean
     */
    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(
                serviceStubs,
                WorkflowClientOptions.newBuilder().setNamespace("default").build()
        );
    }
}
