package com.nageshwarsaini.dynamic.workflows.filter;

import com.nageshwarsaini.dynamic.workflows.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts incoming HTTP requests and extracts the tenant ID
 * from the X-Tenant-ID header, storing it in the ThreadLocal TenantContext.
 *
 * @author nageshwarsaini
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId != null && !tenantId.trim().isEmpty()) {
            TenantContext.setTenantId(tenantId.trim());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clear the context to prevent thread-pool pollution
            TenantContext.clear();
        }
    }
}
