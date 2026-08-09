package com.nageshwarsaini.dynamic.workflows;

import com.fasterxml.jackson.databind.JsonNode;
import com.nageshwarsaini.dynamic.workflows.config.ActivityType;
import com.nageshwarsaini.dynamic.workflows.validator.api.IPreFlightChecksValidator;
import com.nageshwarsaini.dynamic.workflows.worker.DSLWorkflowWorkers;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.converter.EncodedValues;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.DynamicWorkflow;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a dynamic workflow that executes a series of activities
 * based on a provided JSON definition.
 *
 * @author nageshwarsaini
 */
public class DynamicWorkflowImpl implements DynamicWorkflow {

    /**
     * Executes the dynamic workflow logic.
     *
     * @param args the encoded arguments containing the workflow definition and
     *             initial payload
     * @return the resulting execution context map after all activities have been
     *         processed
     */
    @Override
    public Object execute(EncodedValues args) {
        // Extract workflow definition and inputs
        JsonNode workflowDefinition = args.get(0, JsonNode.class);
        JsonNode initialPayload = args.get(1, JsonNode.class);
        Map<String, Object> runtimeContext = new LinkedHashMap<>();
        runtimeContext.put("initialInput", initialPayload);

        // Perform pre-flight checks before entering first activity execution.
        var preFlightChecksValidatorActivity = Workflow.newActivityStub(
                IPreFlightChecksValidator.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(5))
                        .setTaskQueue(DSLWorkflowWorkers.TASK_QUEUE.replace("{TYPE}", ActivityType.SIMPLE.name()))
                        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                        .build());
        var errors = preFlightChecksValidatorActivity.validate(workflowDefinition, initialPayload);

        // Gracefully exit without executing activities
        if (errors != null && errors.has("messages")) {
            runtimeContext.put("status", "FAILED_PREFLIGHT_CHECKS");
            runtimeContext.put("errors", errors.get("messages"));
            return runtimeContext;
        }

        // Proceed with configured activities execution
        JsonNode activitiesArray = workflowDefinition.get("activities");
        for (JsonNode step : activitiesArray) {
            String stepName = step.get("name").asText();
            String executionType = step.has("activityType") ? step.get("activityType").asText() : "SIMPLE";

            ActivityOptions options = ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .setTaskQueue(DSLWorkflowWorkers.TASK_QUEUE.replace("{TYPE}", executionType))
                    .build();
            ActivityStub dynamicActivityStub = Workflow.newUntypedActivityStub(options);

            JsonNode stepResult = dynamicActivityStub.execute(
                    stepName,
                    JsonNode.class,
                    step,
                    runtimeContext);
            String sanitizedKey = stepName.replaceAll("\\s+", "");
            runtimeContext.put(sanitizedKey, stepResult);
        }

        return runtimeContext;
    }
}
