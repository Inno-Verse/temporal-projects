package com.nageshwarsaini.dynamic.workflows.config;

import com.uber.m3.tally.Scope;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TenantWorkflowClientFactoryTest {

    private TenantWorkflowClientFactory factory;
    private WorkflowServiceStubs serviceStubs;

    @BeforeEach
    public void setup() {
        serviceStubs = mock(WorkflowServiceStubs.class);
        Scope mockScope = mock(Scope.class);
        when(mockScope.tagged(any())).thenReturn(mockScope);
        WorkflowServiceStubsOptions mockOptions = WorkflowServiceStubsOptions.newBuilder()
                .setMetricsScope(mockScope)
                .build();
        when(serviceStubs.getOptions()).thenReturn(mockOptions);
        factory = new TenantWorkflowClientFactory(serviceStubs);
    }

    @Test
    public void testGetClientForTenant_ReturnsClient() {
        WorkflowClient client = factory.getClientForTenant("tenant-a");
        assertNotNull(client);
    }

    @Test
    public void testGetClientForTenant_CachesClient() {
        WorkflowClient client1 = factory.getClientForTenant("tenant-a");
        WorkflowClient client2 = factory.getClientForTenant("tenant-a");
        
        assertSame(client1, client2, "Clients for the same tenant should be cached and identical");
    }

    @Test
    public void testGetClientForTenant_DifferentTenants() {
        WorkflowClient clientA = factory.getClientForTenant("tenant-a");
        WorkflowClient clientB = factory.getClientForTenant("tenant-b");
        
        assertNotSame(clientA, clientB, "Clients for different tenants should be different instances");
    }
}
