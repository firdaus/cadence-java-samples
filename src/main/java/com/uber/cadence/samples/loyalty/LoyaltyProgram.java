/*
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.uber.cadence.samples.loyalty;

import java.time.Duration;

public class LoyaltyProgram {

  interface DriverRewardsAction {
    void activate(String driverId);

    void deactivate(String driverId);
  }

  private final String driverId;
  private final DriverRewardsAction driverRewards;
  private final long signUpTime = System.currentTimeMillis();

  private int tripCount;
  private int ratingSum;

  public LoyaltyProgram(DriverRewardsAction driverRewards, String driverId) {
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
