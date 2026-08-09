package com.nageshwarsaini.dynamic.workflows.steps.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class APICallExecutorTest {

    private APICallExecutor apiCallExecutor;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        apiCallExecutor = new APICallExecutor();
        mapper = new ObjectMapper();
    }

    @Test
    public void testGetType() {
        assertEquals("API_CALL", apiCallExecutor.getType());
    }

    @Test
    public void testProcess_GetUserInfo() throws Exception {
        String configJson = "{ \"name\": \"Get User Info\", \"inputs\": { \"endpoint\": \"/user\", \"httpMethod\": \"GET\" } }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        JsonNode result = apiCallExecutor.process(stepConfig, runtimeContext);

        assertNotNull(result);
        assertEquals(54321, result.get("userId").asInt());
        assertEquals("ACTIVE", result.get("status").asText());
    }

    @Test
    public void testProcess_FetchDocumentInfo() throws Exception {
        String configJson = "{ \"name\": \"Fetch Document Info\", \"inputs\": { \"endpoint\": \"/doc\", \"httpMethod\": \"GET\" } }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        JsonNode result = apiCallExecutor.process(stepConfig, runtimeContext);

        assertNotNull(result);
        assertEquals("DOC-999", result.get("documentId").asText());
        assertTrue(result.get("isValid").asBoolean());
    }

    @Test
    public void testProcess_Fallback() throws Exception {
        String configJson = "{ \"name\": \"Other API\", \"inputs\": { \"endpoint\": \"/other\", \"httpMethod\": \"POST\" } }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        JsonNode result = apiCallExecutor.process(stepConfig, runtimeContext);

        assertNotNull(result);
        assertEquals("PROCESSED", result.get("fallbackDefaultStatus").asText());
    }

    @Test
    public void testProcess_MissingInputs() throws Exception {
        String configJson = "{ \"name\": \"Test API\" }";
        JsonNode stepConfig = mapper.readTree(configJson);
        JsonNode runtimeContext = mapper.createObjectNode();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            apiCallExecutor.process(stepConfig, runtimeContext);
        });

        assertTrue(exception.getMessage().contains("missing required 'inputs' object block"));
    }
}
