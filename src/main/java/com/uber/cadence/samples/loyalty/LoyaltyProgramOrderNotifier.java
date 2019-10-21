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

import com.uber.cadence.client.WorkflowClient;
import java.util.Random;

/** Notifies loyalty program workflow about next 10 orders */
public class LoyaltyProgramOrderNotifier {

  public static void main(String[] args) throws Exception {
    WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);

    String customerId = "Customer3";
    LoyaltyProgramWorkflow workflow =
        workflowClient.newWorkflowStub(LoyaltyProgramWorkflow.class, customerId);

    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      String orderId = "order-" + random.nextInt(100);
      System.out.println("Order " + orderId + " placed by " + customerId);
      workflow.onOrder(orderId);
    }
    System.exit(0);
  }
}
