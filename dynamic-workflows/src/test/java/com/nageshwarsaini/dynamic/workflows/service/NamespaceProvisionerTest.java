package com.nageshwarsaini.dynamic.workflows.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.temporal.api.workflowservice.v1.RegisterNamespaceRequest;
import io.temporal.api.workflowservice.v1.RegisterNamespaceResponse;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NamespaceProvisionerTest {

    private NamespaceProvisioner provisioner;
    private WorkflowServiceGrpc.WorkflowServiceBlockingStub blockingStub;

    @BeforeEach
    public void setup() {
        WorkflowServiceStubs serviceStubs = mock(WorkflowServiceStubs.class);
        blockingStub = mock(WorkflowServiceGrpc.WorkflowServiceBlockingStub.class);
        
        when(serviceStubs.blockingStub()).thenReturn(blockingStub);
        provisioner = new NamespaceProvisioner(serviceStubs);
    }

    @Test
    public void testProvisionNamespace_Success() {
        when(blockingStub.registerNamespace(any(RegisterNamespaceRequest.class)))
                .thenReturn(RegisterNamespaceResponse.getDefaultInstance());

        assertDoesNotThrow(() -> provisioner.provisionNamespace("test-namespace"));
        
        verify(blockingStub).registerNamespace(any(RegisterNamespaceRequest.class));
    }

    @Test
    public void testProvisionNamespace_AlreadyExists() {
        StatusRuntimeException exception = new StatusRuntimeException(Status.ALREADY_EXISTS);
        when(blockingStub.registerNamespace(any(RegisterNamespaceRequest.class)))
                .thenThrow(exception);

        // Should not throw an exception because ALREADY_EXISTS is handled gracefully
        assertDoesNotThrow(() -> provisioner.provisionNamespace("existing-namespace"));
        
        verify(blockingStub).registerNamespace(any(RegisterNamespaceRequest.class));
    }

    @Test
    public void testProvisionNamespace_OtherError() {
        StatusRuntimeException exception = new StatusRuntimeException(Status.UNAVAILABLE);
        when(blockingStub.registerNamespace(any(RegisterNamespaceRequest.class)))
                .thenThrow(exception);

        // Should throw RuntimeException for other gRPC errors
        assertThrows(RuntimeException.class, () -> provisioner.provisionNamespace("failed-namespace"));
    }
}
