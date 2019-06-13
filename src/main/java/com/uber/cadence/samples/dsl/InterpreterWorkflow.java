package com.uber.cadence.samples.dsl;

import com.uber.cadence.workflow.QueryMethod;
import com.uber.cadence.workflow.WorkflowMethod;

public interface InterpreterWorkflow {
  @WorkflowMethod
  String execute(String type, String input);

  @QueryMethod
  String getCurrentActivity();
}
