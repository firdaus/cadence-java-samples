package com.uber.cadence.samples.dsl;

import static com.uber.cadence.samples.common.SampleConstants.DOMAIN;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.worker.Worker;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterpreterWorker {
  private static final String TASK_LIST = "Interpreter";

  public static void main(String[] args) {
    // Start a worker that hosts both workflow and activity implementations.
    Worker.Factory factory = new Worker.Factory(DOMAIN);
    Worker worker = factory.newWorker(TASK_LIST);
    // Workflows are stateful. So you need a type to create instances.
    worker.registerWorkflowImplementationTypes(InterpreterWorkflowImpl.class);
    // Activities are stateless and thread safe. So a shared instance is used.
    Map<String, Map<String, String>> definitions = new HashMap<>();
    Map<String, String> definition1 = getDefinition1();
    definitions.put("type1", definition1);
    worker.registerActivitiesImplementations(new SequenceInterpreter(definitions));
    // Start listening to the workflow and activity task lists.
    factory.start();

    // Start a workflow execution. Usually this is done from another program.
    WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);
    // Get a workflow stub using the same task list the worker uses.
    InterpreterWorkflow workflow = workflowClient.newWorkflowStub(InterpreterWorkflow.class);
    // Execute a workflow waiting for it to complete.
    String greeting = workflow.execute("type1", "input1");
    System.out.println(greeting);
    System.exit(0);
  }

  private static Map<String, String> getDefinition1() {
    Map<String, String> definition1 = new HashMap<>();
    List<String> sequence =
        Arrays.asList(
            "Activities::activity1",
            "Activities::activity2",
            "Activities::activity3",
            "Activities::activity4");
    definition1.put("init", sequence.get(0));

    for (int i = 0; i < sequence.size() - 1; i++) {
      definition1.put(sequence.get(i), sequence.get(i + 1));
    }
    definition1.put(sequence.get(sequence.size() - 1), null);
    return definition1;
  }
}
