package com.nageshwarsaini.dynamic.workflows.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TenantContextTest {

    @AfterEach
    public void cleanup() {
        TenantContext.clear();
    }

    @Test
    public void testSetAndGetTenantId() {
        TenantContext.setTenantId("tenant-x");
        assertEquals("tenant-x", TenantContext.getTenantId());
    }

    @Test
    public void testClearTenantId() {
        TenantContext.setTenantId("tenant-y");
        TenantContext.clear();
        assertNull(TenantContext.getTenantId());
    }

    @Test
    public void testThreadLocalIsolation() throws InterruptedException {
        TenantContext.setTenantId("main-tenant");

        Thread t = new Thread(() -> {
            assertNull(TenantContext.getTenantId());
            TenantContext.setTenantId("worker-tenant");
            assertEquals("worker-tenant", TenantContext.getTenantId());
        });

        t.start();
        t.join();

        assertEquals("main-tenant", TenantContext.getTenantId());
    }
}
