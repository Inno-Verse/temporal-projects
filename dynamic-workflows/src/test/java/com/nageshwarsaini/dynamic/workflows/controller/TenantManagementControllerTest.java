package com.nageshwarsaini.dynamic.workflows.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nageshwarsaini.dynamic.workflows.worker.DSLWorkflowWorkers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantManagementController.class)
public class TenantManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DSLWorkflowWorkers workers;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRegisterTenant_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("tenantId", "new-tenant");

        doNothing().when(workers).registerAndStartTenant(anyString());

        mockMvc.perform(post("/api/internal/tenants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully registered tenant: new-tenant"));
    }

    @Test
    public void testRegisterTenant_MissingTenantId() throws Exception {
        Map<String, String> request = new HashMap<>();
        
        mockMvc.perform(post("/api/internal/tenants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("tenantId is required in the request body"));
    }

    @Test
    public void testRegisterTenant_Exception() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("tenantId", "failed-tenant");

        doThrow(new RuntimeException("Connection failed")).when(workers).registerAndStartTenant(anyString());

        mockMvc.perform(post("/api/internal/tenants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to register tenant: Connection failed"));
    }
}
