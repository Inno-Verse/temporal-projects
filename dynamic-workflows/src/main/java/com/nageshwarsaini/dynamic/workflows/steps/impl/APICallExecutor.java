package com.nageshwarsaini.dynamic.workflows.steps.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link IStepExecutor} that handles simulating external API calls.
 *
 * @author nageshwarsaini
 */
@Service
public class APICallExecutor implements IStepExecutor {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Returns the type identifier for this executor.
     *
     * @return the string "API_CALL"
     */
    @Override
    public String getType() {
        return "API_CALL";
    }

    /**
     * Processes an API call step by extracting the endpoint and parameters, and returning a mocked response.
     *
     * @param stepConfig the JSON configuration for the step, containing the API endpoint details
     * @param runtimeContext the current context of the workflow execution
     * @return a JSON node representing the mocked API response
     * @throws IllegalArgumentException if the API configuration is missing or invalid
     */
    @Override
    public JsonNode process(JsonNode stepConfig, JsonNode runtimeContext) {
        String name = stepConfig.get("name").asText();

        JsonNode inputs = stepConfig.get("inputs");
        if (inputs == null || inputs.isNull()) {
            throw new IllegalArgumentException("Activity '" + name + "' is missing required 'inputs' object block.");
        }

        // 1. Extract inputs
        String endpoint = inputs.has("endpoint") ? inputs.get("endpoint").asText() : "UNKNOWN_ENDPOINT";
        String method = inputs.has("httpMethod") ? inputs.get("httpMethod").asText() : "GET";
        JsonNode params = inputs.has("params") ? inputs.get("params") : mapper.createArrayNode();
        System.out.println("Executing API --> Name: " + name + " | Target: " + method + " " + endpoint + "Params: " + params);

        // 2. Process - mock return data
        ObjectNode responseMock = mapper.createObjectNode();
        if ("Get User Info".equals(name) || "Validate User".equals(name)) {
            responseMock.put("userId", 54321);
            responseMock.put("status", "ACTIVE");
        } else if ("Fetch Document Info".equals(name) || "Get Documents".equals(name)) {
            responseMock.put("documentId", "DOC-999");
            responseMock.put("isValid", true);
        } else {
            responseMock.put("fallbackDefaultStatus", "PROCESSED");
        }

        return responseMock;
    }
}