package com.uber.cadence.samples.driverrewards;

import com.uber.cadence.activity.ActivityMethod;

public interface DriverRewardsActivities {
  @ActivityMethod(scheduleToCloseTimeoutSeconds = 10)
  void activate(String driverId);

  @ActivityMethod(scheduleToCloseTimeoutSeconds = 10)
  void deactivate(String driverId);
}
