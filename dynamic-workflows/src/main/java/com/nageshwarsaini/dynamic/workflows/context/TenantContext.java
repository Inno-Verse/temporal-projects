package com.nageshwarsaini.dynamic.workflows.context;

/**
 * Thread-local context to hold the tenant ID for the current request.
 *
 * @author nageshwarsaini
 */
public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * Sets the tenant ID for the current thread.
     *
     * @param tenantId the tenant ID
     */
    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Retrieves the tenant ID for the current thread.
     *
     * @return the tenant ID, or null if not set
     */
    public static String getTenantId() {
        return currentTenant.get();
    }

    /**
     * Clears the tenant ID from the current thread.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
