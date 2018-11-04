package com.uber.cadence.samples.dsl;

import java.util.Map;

public class SequenceInterpreter implements Interpreter {

  private final Map<String, Map<String, String>> definitions;

  public SequenceInterpreter(Map<String, Map<String, String>> definitions) {
    this.definitions = definitions;
  }

  @Override
  public String getNextStep(String workflowType, String lastActivity) {
    Map<String, String> stateTransitions = definitions.get(workflowType);
    return stateTransitions.get(lastActivity);
  }
}
