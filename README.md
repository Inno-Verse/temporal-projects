# temporal-projects
Projects based on Temporal's highly resilient workflow execution model.

## Projects

### 1. [Dynamic Workflows](./dynamic-workflows)
A dynamic, Domain Specific Language (DSL) driven workflow engine built on top of Temporal and Spring Boot. It allows clients to execute complex workflows defined entirely in JSON without needing to write custom Java code for new pipelines.

**Key Features:**
- **JSON DSL Driven**: Define entire workflow sequences, inputs, and strategies purely via JSON payloads.
- **Dynamic Routing**: Uses the Strategy Pattern to dynamically map untyped activities to specific `IStepExecutor` implementations (e.g., REST API calls, inline scripts).
- **Scalable Workers**: Automatically provisions and manages dedicated Temporal workers and task queues based on the configured `ActivityType` (e.g., `SIMPLE`, `COMPLEX`) to effectively isolate resources and manage load.

For comprehensive architecture details, execution flows, and setup instructions, refer to the [Dynamic Workflows README](./dynamic-workflows/README.md).
