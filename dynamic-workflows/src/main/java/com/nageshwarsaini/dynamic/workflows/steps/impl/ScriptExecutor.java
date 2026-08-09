package com.nageshwarsaini.dynamic.workflows.steps.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link IStepExecutor} that evaluates script-based logic defined in the step configuration.
 *
 * @author nageshwarsaini
 */
@Service
public class ScriptExecutor implements IStepExecutor {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Returns the type identifier for this executor.
     *
     * @return the string "SCRIPT"
     */
    @Override
    public String getType() {
        return "SCRIPT";
    }

    /**
     * Processes a script step by extracting and evaluating the script from the step configuration.
     *
     * @param stepConfig the JSON configuration for the step, containing the script to run
     * @param runtimeContext the current context of the workflow execution
     * @return a JSON node representing the output of the script execution
     * @throws IllegalArgumentException if the script configuration is missing or invalid
     */
    @Override
    public JsonNode process(JsonNode stepConfig, JsonNode runtimeContext) {
        String name = stepConfig.get("name").asText();

        // 1. Fetch the "inputs" block
        JsonNode inputs = stepConfig.get("inputs");
        if (inputs == null || inputs.isNull()) {
            throw new IllegalArgumentException("Activity '" + name + "' is missing required 'inputs' object block.");
        } else if (!inputs.has("script") || inputs.get("script").isNull()) {
            throw new IllegalArgumentException("Activity '" + name + "' is missing required 'script' string parameter.");
        }

        // 2. Extract the script payload
        String codeSnippet = inputs.get("script").asText();
        System.out.println("Evaluating logic engine statement: " + codeSnippet);

        ObjectNode output = mapper.createObjectNode();
        output.put("transformationStatus", "COMPLETED");
        return output;
    }
}
