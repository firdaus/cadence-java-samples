package com.uber.cadence.samples.dsl;

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.workflow.ActivityStub;
import com.uber.cadence.workflow.Workflow;
import java.time.Duration;

public class InterpreterWorkflowImpl implements InterpreterWorkflow {

  private final Interpreter interpreter = Workflow.newActivityStub(Interpreter.class);

  private final ActivityStub activities =
      Workflow.newUntypedActivityStub(
          new ActivityOptions.Builder().setScheduleToCloseTimeout(Duration.ofMinutes(10)).build());

  private String currentActivity;
  private String lastActivityResult;

  @Override
  public String execute(String workflowType, String input) {
    do {
      currentActivity = interpreter.getNextStep(workflowType, currentActivity);
      lastActivityResult = activities.execute(currentActivity, String.class, lastActivityResult);
    } while (currentActivity != null);
    return lastActivityResult;
  }

  @Override
  public String getCurrentActivity() {
    return currentActivity;
  }
}
