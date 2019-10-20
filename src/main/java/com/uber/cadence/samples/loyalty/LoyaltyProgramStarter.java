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

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;

import static com.uber.cadence.samples.common.SampleConstants.DOMAIN;

/** Starts a loyalty program workflow. */
public class LoyaltyProgramStarter {

  public static void main(String[] args) throws Exception {
    WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);

    String driverId = "Driver3";
    WorkflowOptions options = new WorkflowOptions.Builder().setWorkflowId(driverId).build();
    LoyaltyProgramWorkflow workflow =
        workflowClient.newWorkflowStub(LoyaltyProgramWorkflow.class, options);

    System.out.println("Starting Driver Rewards for " + driverId);
    WorkflowClient.start(workflow::driverRewards, driverId);

    System.out.println("DriverRewards started for " + driverId);
    System.exit(0);
  }
}
