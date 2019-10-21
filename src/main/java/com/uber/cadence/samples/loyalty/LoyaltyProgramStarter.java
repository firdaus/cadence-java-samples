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

import static com.uber.cadence.samples.common.SampleConstants.DOMAIN;
import static com.uber.cadence.samples.loyalty.LoyaltyProgramWorkflowWorker.TASK_LIST;

import com.uber.cadence.WorkflowIdReusePolicy;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import java.time.Duration;
import java.util.Collections;

/** Starts a loyalty program workflow. */
public class LoyaltyProgramStarter {

  public static void main(String[] args) throws Exception {
    WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);

    String customerId = "Customer3";
    WorkflowOptions options =
        new WorkflowOptions.Builder()
            .setWorkflowId(customerId)
            .setTaskList(TASK_LIST)
            .setExecutionStartToCloseTimeout(Duration.ofDays(365 * 2))
            .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.AllowDuplicate)
            .build();
    LoyaltyProgramWorkflow workflow =
        workflowClient.newWorkflowStub(LoyaltyProgramWorkflow.class, options);

    System.out.println("Starting Loyalty program for " + customerId);
    WorkflowClient.start(workflow::loyaltyProgram, customerId, Collections.emptyList());

    System.out.println("LoyaltyProgram started for " + customerId);
    System.exit(0);
  }
}
