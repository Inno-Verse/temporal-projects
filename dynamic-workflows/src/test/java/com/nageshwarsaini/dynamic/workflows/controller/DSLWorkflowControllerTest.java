package com.nageshwarsaini.dynamic.workflows.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nageshwarsaini.dynamic.workflows.dto.WorkflowRequest;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DSLWorkflowController.class)
public class DSLWorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowClient workflowClient;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testExecuteWorkflow() throws Exception {
        // Prepare mock request
        WorkflowRequest request = new WorkflowRequest();
        request.setWorkflowName("TestWorkflow");
        request.setDefinition(mapper.createObjectNode());
        request.setInput(mapper.createObjectNode());

        // Prepare mock Temporal behavior
        WorkflowStub mockStub = mock(WorkflowStub.class);
        WorkflowExecution mockExecution = WorkflowExecution.newBuilder().setWorkflowId("test-id").build();

        when(workflowClient.newUntypedWorkflowStub(anyString(), any(WorkflowOptions.class))).thenReturn(mockStub);
        when(mockStub.start(any(), any())).thenReturn(mockExecution);
        
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("status", "SUCCESS");
        when(mockStub.getResult(Map.class)).thenReturn(mockResult);

        // Execute request
        mockMvc.perform(post("/api/workflows/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
    
    @Test
    public void testExecuteWorkflow_Exception() throws Exception {
        // Prepare mock request
        WorkflowRequest request = new WorkflowRequest();
        request.setWorkflowName("TestWorkflow");
        
        when(workflowClient.newUntypedWorkflowStub(anyString(), any(WorkflowOptions.class)))
            .thenThrow(new RuntimeException("Temporal cluster down"));

        // Execute request
        mockMvc.perform(post("/api/workflows/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
