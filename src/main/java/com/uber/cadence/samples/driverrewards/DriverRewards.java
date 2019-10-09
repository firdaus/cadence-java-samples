package com.uber.cadence.samples.driverrewards;

import java.time.Duration;

public class DriverRewards {

  interface DriverRewardsAction {
    void activate(String driverId);

    void deactivate(String driverId);
  }

  private final String driverId;
  private final DriverRewardsAction driverRewards;
  private final long signUpTime = System.currentTimeMillis();

  private int tripCount;
  private int ratingSum;

  public DriverRewards(DriverRewardsAction driverRewards, String driverId) {
    this.driverRewards = driverRewards;
    this.driverId = driverId;
  }

  /** Called once per trip completion */
  public void onTrip(int rating) {
    tripCount++;
    ratingSum += rating;
  }

  /** Called when driver signs up for the rewards program. */
  public void driverRewards() throws InterruptedException {
    while (System.currentTimeMillis() < signUpTime + Duration.ofDays(365).toMillis()) {
      reset();
      Thread.sleep(Duration.ofDays(30).toMillis());
      if (checkEligibility()) {
        driverRewards.activate(driverId);
      } else {
        driverRewards.deactivate(driverId);
        break;
      }
    }
  }

  private void reset() {
    tripCount = 0;
    ratingSum = 0;
  }

  private boolean checkEligibility() {
    if (tripCount < 20) {
      return false;
    }
    float avgRating = ((float) ratingSum) / tripCount;
    return avgRating > 4.0;
  }
}
