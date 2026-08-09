package com.nageshwarsaini.dynamic.workflows.config;


import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Temporal client and service stubs.
 *
 * @author nageshwarsaini
 */
@Configuration
public class TemporalConfig {

    @Value("${temporal.target-endpoint:localhost:7233}")
    private String targetEndpoint;

    /**
     * Creates and provides a {@link WorkflowServiceStubs} instance connecting to the local Temporal server.
     *
     * @return a configured {@link WorkflowServiceStubs} bean
     */
    @Bean(destroyMethod = "shutdown")
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(targetEndpoint).build()
        );
    }

}
