package com.nageshwarsaini.dynamic.workflows.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory and cache for creating WorkflowClient instances per tenant namespace.
 *
 * @author nageshwarsaini
 */
@Component
public class TenantWorkflowClientFactory {

    private final Map<String, WorkflowClient> clientCache = new ConcurrentHashMap<>();
    private final WorkflowServiceStubs serviceStubs;

    public TenantWorkflowClientFactory(WorkflowServiceStubs serviceStubs) {
        this.serviceStubs = serviceStubs;
    }

    /**
     * Retrieves or creates a WorkflowClient for the specified tenant ID.
     *
     * @param tenantId the tenant ID (namespace)
     * @return the WorkflowClient configured for the tenant
     */
    public WorkflowClient getClientForTenant(String tenantId) {
        return clientCache.computeIfAbsent(tenantId, id -> {
            WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
                    .setNamespace(id)
                    .build();
            return WorkflowClient.newInstance(serviceStubs, options);
        });
    }
}
