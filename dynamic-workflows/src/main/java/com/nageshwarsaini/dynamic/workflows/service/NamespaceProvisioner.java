package com.nageshwarsaini.dynamic.workflows.service;

import com.google.protobuf.Duration;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.temporal.api.workflowservice.v1.RegisterNamespaceRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Service;

/**
 * Service to dynamically provision Temporal namespaces.
 *
 * @author nageshwarsaini
 */
@Service
public class NamespaceProvisioner {

    private final WorkflowServiceStubs serviceStubs;

    public NamespaceProvisioner(WorkflowServiceStubs serviceStubs) {
        this.serviceStubs = serviceStubs;
    }

    /**
     * Provisions a new namespace in the Temporal server if it doesn't already exist.
     *
     * @param tenantId the namespace name
     */
    public void provisionNamespace(String tenantId) {
        RegisterNamespaceRequest request = RegisterNamespaceRequest.newBuilder()
                .setNamespace(tenantId)
                .setWorkflowExecutionRetentionPeriod(Duration.newBuilder().setSeconds(3 * 24 * 60 * 60).build()) // 3 days retention
                .build();
        try {
            serviceStubs.blockingStub().registerNamespace(request);
            System.out.println("Successfully provisioned new namespace in Temporal: " + tenantId);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                System.out.println("Namespace already exists in Temporal: " + tenantId);
            } else {
                throw new RuntimeException("Failed to provision namespace " + tenantId, e);
            }
        }
    }
}
