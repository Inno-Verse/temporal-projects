package com.nageshwarsaini.dynamic.workflows.validator.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreFlightChecksValidatorTest {

    private PreFlightChecksValidator validator;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        validator = new PreFlightChecksValidator();
        mapper = new ObjectMapper();
    }

    @Test
    public void testValidate_Success() throws Exception {
        String definitionStr = "{ \"activities\": [] }";
        String inputStr = "{ \"requestingUser\": \"Nageshwar Saini\" }";
        
        JsonNode definition = mapper.readTree(definitionStr);
        JsonNode inputs = mapper.readTree(inputStr);

        JsonNode result = validator.validate(definition, inputs);

        assertTrue(result.isEmpty(), "Result should be empty for valid inputs");
    }

    @Test
    public void testValidate_Failure_MissingActivities() throws Exception {
        String definitionStr = "{}";
        String inputStr = "{ \"requestingUser\": \"Nageshwar Saini\" }";
        
        JsonNode definition = mapper.readTree(definitionStr);
        JsonNode inputs = mapper.readTree(inputStr);

        JsonNode result = validator.validate(definition, inputs);

        assertFalse(result.isEmpty(), "Result should not be empty");
        assertTrue(result.has("messages"));
        assertTrue(result.get("messages").asText().contains("Workflow payload definition must contain a valid activities array."));
    }

    @Test
    public void testValidate_Failure_InvalidUser() throws Exception {
        String definitionStr = "{ \"activities\": [] }";
        String inputStr = "{ \"requestingUser\": \"Unknown User\" }";
        
        JsonNode definition = mapper.readTree(definitionStr);
        JsonNode inputs = mapper.readTree(inputStr);

        JsonNode result = validator.validate(definition, inputs);

        assertFalse(result.isEmpty(), "Result should not be empty");
        assertTrue(result.has("messages"));
        assertTrue(result.get("messages").asText().contains("Invalid user."));
    }
}
