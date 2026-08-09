package com.nageshwarsaini.dynamic.workflows.controller;

import com.nageshwarsaini.dynamic.workflows.config.TenantWorkflowClientFactory;
import com.nageshwarsaini.dynamic.workflows.context.TenantContext;
import com.nageshwarsaini.dynamic.workflows.dto.WorkflowRequest;
import com.nageshwarsaini.dynamic.workflows.worker.DSLWorkflowWorkers;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for exposing endpoints to trigger dynamic DSL-based workflows.
 *
 * @author nageshwarsaini
 */
@RestController
@RequestMapping("/api/workflows")
public class DSLWorkflowController {

    private final TenantWorkflowClientFactory clientFactory;

    /**
     * Constructs a new {@code DSLWorkflowController} with the specified client factory.
     *
     * @param clientFactory the factory to get workflow clients per tenant
     */
    public DSLWorkflowController(TenantWorkflowClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * Triggers the execution of a dynamic workflow based on the provided request.
     *
     * @param request the workflow request containing the name, definition, and input
     * @return a {@link ResponseEntity} containing the final context map resulting from the workflow execution
     */
    @PostMapping("/execute")
    public ResponseEntity<?> executeWorkflow(@RequestBody WorkflowRequest request) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.badRequest().body("Missing X-Tenant-ID header");
        }

        try {
            WorkflowClient client = clientFactory.getClientForTenant(tenantId);
            
            // Generate a unique workflow ID tracking string for this run
            String workflowId = "dsl-" + request.getWorkflowName().toLowerCase() + "-" + UUID.randomUUID();

            // Configure untyped execution matching your DSLWorkflowWorkers queue parameters
            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue(DSLWorkflowWorkers.TASK_QUEUE.replace("{TYPE}", "SIMPLE"))
                    .setWorkflowId(workflowId)
                    .build();

            WorkflowStub untypedStub = client.newUntypedWorkflowStub(request.getWorkflowName(), options);
            System.out.println(">>> REST Endpoint API triggered for tenant " + tenantId + ". Dispatching payload: " + workflowId);

            // Run execution asynchronously
            WorkflowExecution execution = untypedStub.start(
                    request.getDefinition(),
                    request.getInput()
            );

            System.out.println("Started workflow with ID: " + execution.getWorkflowId());

            // Synchronously block and wait for the final structural context map to return
            @SuppressWarnings("unchecked")
            Map<String, Object> finalContextMap = untypedStub.getResult(Map.class);

            return ResponseEntity.ok(finalContextMap);

        } catch (Exception e) {
            System.err.println("Failed to execute dynamic pipeline: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
