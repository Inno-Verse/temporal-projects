package com.nageshwarsaini.dynamic.workflows.filter;

import com.nageshwarsaini.dynamic.workflows.context.TenantContext;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TenantFilterTest {

    private final TenantFilter filter = new TenantFilter();

    @AfterEach
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void testDoFilterInternal_WithTenantHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", "tenant-alpha");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // TenantContext should be cleared after filter execution
        assertNull(TenantContext.getTenantId());
    }

    @Test
    public void testDoFilterInternal_SetsContextDuringExecution() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", "tenant-beta");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        FilterChain filterChain = (req, res) -> {
            // Verify context is set during the filter chain execution
            assertEquals("tenant-beta", TenantContext.getTenantId());
        };

        filter.doFilterInternal(request, response, filterChain);
        
        // Verify cleared afterwards
        assertNull(TenantContext.getTenantId());
    }

    @Test
    public void testDoFilterInternal_NoHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        FilterChain filterChain = (req, res) -> {
            assertNull(TenantContext.getTenantId());
        };

        filter.doFilterInternal(request, response, filterChain);
    }
}
