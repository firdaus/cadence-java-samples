package com.uber.cadence.samples.dsl;

import java.util.List;
import java.util.Map;

public class ActivitySequenceInterpreter implements Interpreter {

  private final Map<String, List<String>> definitions;

  public ActivitySequenceInterpreter(Map<String, List<String>> definitions) {
    this.definitions = definitions;
  }

  @Override
  public String getNextStep(String workflowType, String lastActivity) {
    List<String> sequence = definitions.get(workflowType);
    if (lastActivity == null) {
      return sequence.get(0);
    }
    int i = sequence.indexOf(lastActivity);
    if (i >= sequence.size()) {
      return null;
    }
    return sequence.get(i + 1);
  }
}
