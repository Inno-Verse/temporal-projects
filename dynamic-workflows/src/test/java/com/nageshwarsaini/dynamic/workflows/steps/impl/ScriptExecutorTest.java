package com.nageshwarsaini.dynamic.workflows.steps.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptExecutorTest {

    private ScriptExecutor scriptExecutor;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        scriptExecutor = new ScriptExecutor();
        mapper = new ObjectMapper();
    }

    @Test
    public void testGetType() {
        assertEquals("SCRIPT", scriptExecutor.getType());
    }

    @Test
    public void testProcess_Success() throws Exception {
        String configJson = "{ \"name\": \"Test Script\", \"inputs\": { \"script\": \"return true;\" } }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        JsonNode result = scriptExecutor.process(stepConfig, runtimeContext);

        assertNotNull(result);
        assertEquals("COMPLETED", result.get("transformationStatus").asText());
    }

    @Test
    public void testProcess_MissingInputs() throws Exception {
        String configJson = "{ \"name\": \"Test Script\" }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scriptExecutor.process(stepConfig, runtimeContext);
        });

        assertTrue(exception.getMessage().contains("missing required 'inputs' object block"));
    }

    @Test
    public void testProcess_MissingScript() throws Exception {
        String configJson = "{ \"name\": \"Test Script\", \"inputs\": {} }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scriptExecutor.process(stepConfig, runtimeContext);
        });

        assertTrue(exception.getMessage().contains("missing required 'script' string parameter"));
    }
}
