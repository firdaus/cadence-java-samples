package com.uber.cadence.samples.dsl;

import com.uber.cadence.activity.ActivityMethod;

public interface Interpreter {
  @ActivityMethod
  String getNextStep(String workflowType, String lastActivity);
}
