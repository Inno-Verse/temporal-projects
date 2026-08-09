package com.nageshwarsaini.dynamic.workflows.validator.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/** This class is used to validate inputs to the workflow before the activity execution starts.
 * @author nageshwarsaini
 */
@ActivityInterface
public interface IPreFlightChecksValidator {

    /**
     * Validates the workflow inputs before entering the first activity execution.
     */
    @ActivityMethod
    JsonNode validate(JsonNode workflowDefinition, JsonNode initialPayload);
}
