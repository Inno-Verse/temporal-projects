package com.nageshwarsaini.dynamic.workflows.validator.impl;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nageshwarsaini.dynamic.workflows.validator.api.IPreFlightChecksValidator;

import java.util.ArrayList;
import java.util.List;

/** This class is used to validate inputs to the workflow before the activity execution starts.
 * @author nageshwarsaini
 */
public class PreFlightChecksValidator implements IPreFlightChecksValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public JsonNode validate(JsonNode workflowDefinition, JsonNode workflowInputs) {
        var validRequestingUser = "Nageshwar Saini";
        List<String> errors = new ArrayList<>();
        JsonNode activitiesArray = workflowDefinition.get("activities");
        if (activitiesArray == null || !activitiesArray.isArray()) {
            errors.add("Workflow payload definition must contain a valid activities array.");
        }
        if(!workflowInputs.has("requestingUser") || !validRequestingUser.equals(workflowInputs.get("requestingUser").asText())) {
            errors.add("Invalid user. Only " + validRequestingUser + " can request this workflow");
        }
        ObjectNode mockErrors = mapper.createObjectNode();
        if(!errors.isEmpty()) {
            mockErrors.put("messages", errors.toString());
        }
        return mockErrors;
    }
}
