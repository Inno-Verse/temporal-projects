package com.nageshwarsaini.dynamic.workflows.activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import io.temporal.activity.Activity;
import io.temporal.activity.DynamicActivity;
import io.temporal.common.converter.EncodedValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a dynamic activity that routes execution to a specific {@link IStepExecutor}
 * based on the configuration of the step.
 *
 * @author nageshwarsaini
 */
public class DynamicActivityImpl implements DynamicActivity {

    private final Map<String, IStepExecutor> executors = new HashMap<>();

    /**
     * Constructs a new {@code DynamicActivityImpl} with the given list of step executors.
     *
     * @param executorList the list of available step executors
     */
    public DynamicActivityImpl(List<IStepExecutor> executorList) {
        executorList.forEach(executor -> executors.put(executor.getType(), executor));
    }

    /**
     * Executes the dynamic activity by routing it to the appropriate executor.
     *
     * @param args the encoded arguments containing the step configuration and runtime context
     * @return the result of the executed step
     * @throws IllegalArgumentException if no suitable executor is found for the step type
     */
    @Override
    public Object execute(EncodedValues args) {
        String displayActivityName = Activity.getExecutionContext().getInfo().getActivityType();
        JsonNode stepConfig = args.get(0, JsonNode.class);
        JsonNode runtimeContext = args.get(1, JsonNode.class);

        String routingType = stepConfig.get("type").asText();
        System.out.println("Processing step [" + displayActivityName + "] via strategy: " + routingType);

        IStepExecutor executor = executors.get(routingType);
        if (executor == null) {
            throw new IllegalArgumentException("No strategy class registered for type: " + routingType);
        }

        try {
            return executor.process(stepConfig, runtimeContext);
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR inside step [" + displayActivityName + "]: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

}
