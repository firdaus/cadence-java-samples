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

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import com.uber.cadence.samples.fileprocessing.FileProcessingWorkflow;

import java.net.URL;

import static com.uber.cadence.samples.common.SampleConstants.DOMAIN;

/** Starts a driver rewards sample workflow. */
public class DriverRewardsStarter {

  public static void main(String[] args) throws Exception {
    WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);

    String driverId = "Driver3";
    WorkflowOptions options = new WorkflowOptions.Builder().setWorkflowId(driverId).build();
    DriverRewardsWorkflow workflow =
            workflowClient.newWorkflowStub(DriverRewardsWorkflow.class, options);

    System.out.println("Starting Driver Rewards for " + driverId);
    WorkflowClient.start(
            workflow::driverRewards,
            driverId
    );

    System.out.println("DriverRewards started for " + driverId);
    System.exit(0);
  }
}
