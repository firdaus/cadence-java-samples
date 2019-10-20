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

import com.uber.cadence.workflow.Workflow;

import java.time.Duration;

/**
 * Implements driver rewards business logic as a workflow. Compare to a non fault tolerant
 * implementation.
 *
 * @see LoyaltyProgram
 */
public final class LoyaltyProgramWorkflowImpl implements LoyaltyProgramWorkflow {

  /** Stub used to invoke activities through a strongly typed interface. */
  private final LoyaltyProgramActivities driverRewards =
      Workflow.newActivityStub(LoyaltyProgramActivities.class);
  /**
   * Time workflow started execution. Note that Workflow.currentTimeMillis must be used instead of
   * System.currentTimeMillis in the workflow code.
   */
  private final long signUpTime = Workflow.currentTimeMillis();
  /** Total trip count for the current month. */
  private int tripCount;
  /**
   * Total sum of ratings for the current month. Used to calculate the average rating for the month.
   */
  private int ratingSum;

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
      // Workflow.sleep must be used instead of Thread.sleep in the workflow code.
      // It is OK to sleep for long period of time in production code.
      // The workflow does not consume any worker resources while sleeping.
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
