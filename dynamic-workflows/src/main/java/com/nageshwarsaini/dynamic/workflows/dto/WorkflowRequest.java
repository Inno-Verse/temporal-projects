package com.nageshwarsaini.dynamic.workflows.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data Transfer Object representing a request to execute a dynamic workflow.
 *
 * @author nageshwarsaini
 */
public class WorkflowRequest {
    private String workflowName;
    private JsonNode definition;
    private JsonNode input;

    // Standard Getters and Setters

    /**
     * Retrieves the name of the workflow.
     *
     * @return the workflow name
     */
    public String getWorkflowName() { return workflowName; }

    /**
     * Sets the name of the workflow.
     *
     * @param workflowName the workflow name to set
     */
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }

    /**
     * Retrieves the workflow definition in JSON format.
     *
     * @return the workflow definition
     */
    public JsonNode getDefinition() { return definition; }

    /**
     * Sets the workflow definition.
     *
     * @param definition the JSON node representing the workflow definition
     */
    public void setDefinition(JsonNode definition) { this.definition = definition; }

    /**
     * Retrieves the input payload for the workflow.
     *
     * @return the initial input data
     */
    public JsonNode getInput() { return input; }

    /**
     * Sets the input payload for the workflow.
     *
     * @param input the JSON node representing the initial input data
     */
    public void setInput(JsonNode input) { this.input = input; }
}
