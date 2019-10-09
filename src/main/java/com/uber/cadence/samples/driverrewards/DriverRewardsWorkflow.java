package com.uber.cadence.samples.driverrewards;

import static com.uber.cadence.samples.driverrewards.DriverRewardsWorker.TASK_LIST;

import com.uber.cadence.workflow.QueryMethod;
import com.uber.cadence.workflow.SignalMethod;
import com.uber.cadence.workflow.WorkflowMethod;

public interface DriverRewardsWorkflow {
  @WorkflowMethod(executionStartToCloseTimeoutSeconds = 3600 * 24 * 365, taskList = TASK_LIST)
  void driverRewards(String driverId);

  @SignalMethod
  void onTrip(int rating);

  @QueryMethod
  float getRating();
}
