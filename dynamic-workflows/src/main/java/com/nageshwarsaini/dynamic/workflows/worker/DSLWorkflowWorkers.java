package com.nageshwarsaini.dynamic.workflows.worker;

import com.nageshwarsaini.dynamic.workflows.DynamicWorkflowImpl;
import com.nageshwarsaini.dynamic.workflows.activity.DynamicActivityImpl;
import com.nageshwarsaini.dynamic.workflows.config.ActivityType;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Manages the initialization and lifecycle of Temporal workers for dynamic DSL workflows.
 *
 * @author nageshwarsaini
 */
@Component
public class DSLWorkflowWorkers {
    public static final String TASK_QUEUE = "{TYPE}_EXECUTION_QUEUE";

    private final List<IStepExecutor> executors;
    private final WorkflowClient client;

    private WorkerFactory factory;

    /**
     * Constructs a new {@code DSLWorkflowWorkers} instance.
     *
     * @param executors the list of registered step executors
     * @param client the workflow client to use for creating the worker factory
     */
    public DSLWorkflowWorkers(List<IStepExecutor> executors, WorkflowClient client) {
        this.executors = executors;
        this.client = client;
    }

    /**
     * Initializes the Temporal worker factory and starts polling for tasks on all activity queues.
     */
    @PostConstruct
    public void init() {
        this.factory = WorkerFactory.newInstance(client);
        for(var type: ActivityType.values()) {
            Worker worker = factory.newWorker(TASK_QUEUE.replace("{TYPE}", type.name()));
            worker.registerWorkflowImplementationTypes(DynamicWorkflowImpl.class);
            worker.registerActivitiesImplementations(new DynamicActivityImpl(executors));
        }
        factory.start();
        System.out.println("DSL Engine Worker successfully started and polling " + TASK_QUEUE + "...");
    }

    /**
     * Gracefully shuts down the Temporal worker factory upon application destruction.
     */
    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down Temporal worker polling loop...");
        if (factory != null) {
            factory.shutdown(); // Closes the polling worker, but leaves the central client channel intact
        }
    }
}
