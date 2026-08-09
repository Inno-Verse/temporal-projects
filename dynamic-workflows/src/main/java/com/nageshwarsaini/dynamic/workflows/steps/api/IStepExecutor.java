package com.nageshwarsaini.dynamic.workflows.steps.api;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface representing a strategy for executing a specific type of workflow step.
 *
 * @author nageshwarsaini
 */
public interface IStepExecutor {

    /**
     * @return Returns the type name this processor handles (e.g., "API_CALL", "SCRIPT")
     */
    String getType();

    /**
     * Executes the actual backend logic
     * @param stepConfig Step specific configurations and inputs
     * @param runtimeContext Collective context of the workflow
     * @return Step output
     */
    JsonNode process(JsonNode stepConfig, JsonNode runtimeContext);
}
