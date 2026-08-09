package com.nageshwarsaini.dynamic.workflows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nageshwarsaini.dynamic.workflows.activity.DynamicActivityImpl;
import com.nageshwarsaini.dynamic.workflows.config.ActivityType;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import com.nageshwarsaini.dynamic.workflows.worker.DSLWorkflowWorkers;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DynamicWorkflowTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private WorkflowClient workflowClient;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        
        IStepExecutor mockExecutor = mock(IStepExecutor.class);
        when(mockExecutor.getType()).thenReturn("MOCK_TYPE");
        try {
            when(mockExecutor.process(any(), any())).thenAnswer(invocation -> {
                ObjectMapper m = new ObjectMapper();
                return m.createObjectNode().put("status", "SUCCESS");
            });
        } catch (Exception e) {
            // ignore
        }

        worker = testEnv.newWorker(DSLWorkflowWorkers.TASK_QUEUE.replace("{TYPE}", ActivityType.SIMPLE.name()));
        worker.registerWorkflowImplementationTypes(DynamicWorkflowImpl.class);
        worker.registerActivitiesImplementations(new DynamicActivityImpl(Collections.singletonList(mockExecutor)));
        
        testEnv.start();
        workflowClient = testEnv.getWorkflowClient();
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    public void testDynamicWorkflowExecution() throws Exception {
        String definitionJson = "{ \"activities\": [ { \"name\": \"Step 1\", \"type\": \"MOCK_TYPE\" } ] }";
        JsonNode definition = mapper.readTree(definitionJson);
        JsonNode input = mapper.createObjectNode().put("key", "value");

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(DSLWorkflowWorkers.TASK_QUEUE.replace("{TYPE}", ActivityType.SIMPLE.name()))
                .build();

        WorkflowStub workflow = workflowClient.newUntypedWorkflowStub("TestWorkflow", options);
        workflow.start(definition, input);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = workflow.getResult(Map.class);

        assertNotNull(result);
        assertTrue(result.containsKey("initialInput"));
        assertTrue(result.containsKey("Step1"));
        
        Map<String, Object> step1Result = (Map<String, Object>) result.get("Step1");
        assertEquals("SUCCESS", step1Result.get("status"));
    }
}
