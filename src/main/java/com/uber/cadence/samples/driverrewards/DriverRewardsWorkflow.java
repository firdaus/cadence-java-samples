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

package com.uber.cadence.samples.driverrewards;

import static com.uber.cadence.samples.driverrewards.DriverRewardsWorker.TASK_LIST;

import com.uber.cadence.workflow.QueryMethod;
import com.uber.cadence.workflow.SignalMethod;
import com.uber.cadence.workflow.WorkflowMethod;

/** Driver rewards workflow interface. */
public interface DriverRewardsWorkflow {

  /**
   * Main workflow method called when workflow is started.
   *
   * @param driverId unique driver id.
   */
  @WorkflowMethod(executionStartToCloseTimeoutSeconds = 3600 * 24 * 365, taskList = TASK_LIST)
  void driverRewards(String driverId);

  /**
   * Called upon each trip completion.
   *
   * @param rating trip driver rating from 1 to 5.
   */
  @SignalMethod
  void onTrip(int rating);

  /** Returns average driver rating for the current time period. */
  @QueryMethod
  float getRating();
}
