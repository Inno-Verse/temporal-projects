package com.nageshwarsaini.dynamic.workflows.worker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nageshwarsaini.dynamic.workflows.DynamicWorkflowImpl;
import com.nageshwarsaini.dynamic.workflows.activity.DynamicActivityImpl;
import com.nageshwarsaini.dynamic.workflows.config.ActivityType;
import com.nageshwarsaini.dynamic.workflows.config.TenantWorkflowClientFactory;
import com.nageshwarsaini.dynamic.workflows.service.NamespaceProvisioner;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import com.nageshwarsaini.dynamic.workflows.validator.impl.PreFlightChecksValidator;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the initialization and lifecycle of Temporal workers for dynamic DSL workflows per tenant.
 *
 * @author nageshwarsaini
 */
@Component
public class DSLWorkflowWorkers {
    public static final String TASK_QUEUE = "{TYPE}_EXECUTION_QUEUE";

    private final List<IStepExecutor> executors;
    private final TenantWorkflowClientFactory clientFactory;
    private final NamespaceProvisioner namespaceProvisioner;
    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, WorkerFactory> activeFactories = new ConcurrentHashMap<>();

    public DSLWorkflowWorkers(List<IStepExecutor> executors, 
                              TenantWorkflowClientFactory clientFactory, 
                              NamespaceProvisioner namespaceProvisioner) {
        this.executors = executors;
        this.clientFactory = clientFactory;
        this.namespaceProvisioner = namespaceProvisioner;
    }

    /**
     * Reads tenants.json and starts workers for predefined tenants.
     */
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("tenants.json");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    JsonNode tenantsNode = mapper.readTree(is).get("tenants");
                    if (tenantsNode != null && tenantsNode.isArray()) {
                        for (JsonNode tenant : tenantsNode) {
                            registerAndStartTenant(tenant.asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read predefined tenants: " + e.getMessage());
        }
    }

    /**
     * Provisions a namespace and starts a worker polling loop for a given tenant.
     *
     * @param tenantId the tenant ID
     */
    public void registerAndStartTenant(String tenantId) {
        if (activeFactories.containsKey(tenantId)) {
            System.out.println("Workers already polling for tenant: " + tenantId);
            return;
        }

        System.out.println("Starting provisioning for tenant: " + tenantId);
        namespaceProvisioner.provisionNamespace(tenantId);
        
        WorkflowClient tenantClient = clientFactory.getClientForTenant(tenantId);
        WorkerFactory factory = WorkerFactory.newInstance(tenantClient);
        
        for(var type: ActivityType.values()) {
            Worker worker = factory.newWorker(TASK_QUEUE.replace("{TYPE}", type.name()));
            worker.registerWorkflowImplementationTypes(DynamicWorkflowImpl.class);
            worker.registerActivitiesImplementations(new DynamicActivityImpl(executors), new PreFlightChecksValidator());
        }
        
        factory.start();
        activeFactories.put(tenantId, factory);
        System.out.println("DSL Engine Workers successfully started and polling for tenant " + tenantId + " on queues...");
    }

    /**
     * Gracefully shuts down all active Temporal worker factories.
     */
    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down all active Temporal worker polling loops...");
        activeFactories.forEach((tenant, factory) -> {
            try {
                factory.shutdown();
                System.out.println("Shut down factory for tenant: " + tenant);
            } catch (Exception e) {
                System.err.println("Failed to shutdown factory for tenant " + tenant + ": " + e.getMessage());
            }
        });
    }
}
