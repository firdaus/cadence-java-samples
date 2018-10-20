package com.uber.cadence.samples.driverrewards;

import com.uber.cadence.workflow.Workflow;

import java.time.Duration;

public class DriverRewardsWorkflowImpl implements DriverRewardsWorkflow {

  private final DriverRewardsActivities driverRewards;
  private final long signUpTime = Workflow.currentTimeMillis();
  private int tripCount;
  private int ratingSum;

  public DriverRewardsWorkflowImpl() {
    driverRewards = Workflow.newActivityStub(DriverRewardsActivities.class);
  }

  @Override
  public void onTrip(int rating) {
    tripCount++;
    ratingSum += rating;
  }

  @Override
  public float getRating() {
    return tripCount == 0 ? -1 : ((float) ratingSum) / tripCount;
  }

  @Override
  public void driverRewards(String driverId) {
    while (!expired()) {
      reset();
      Workflow.sleep(Duration.ofDays(30));
      if (checkEligibility()) {
        driverRewards.activate(driverId);
      } else {
        driverRewards.deactivate(driverId);
        break;
      }
    }
  }

  private boolean expired() {
    return Workflow.currentTimeMillis() > signUpTime + Duration.ofDays(365).toMillis();
  }

  private boolean checkEligibility() {
    return getRating() > 4.0 && tripCount > 20;
  }

  private void reset() {
    tripCount = 0;
    ratingSum = 0;
  }
}
