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

import static com.uber.cadence.samples.loyalty.LoyaltyProgramWorkflowWorker.TASK_LIST;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import com.uber.cadence.testing.TestWorkflowEnvironment;
import com.uber.cadence.worker.Worker;
import java.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoyaltyProgramWorkflowTest {

  String customerId = "customer1";
  private TestWorkflowEnvironment testEnv;
  private Worker worker;
  private WorkflowClient workflowClient;

  @Before
  public void setUp() {
    testEnv = TestWorkflowEnvironment.newInstance();
    worker = testEnv.newWorker(TASK_LIST);
    worker.registerWorkflowImplementationTypes(LoyaltyProgramWorkflowImpl.class);
    workflowClient = testEnv.newWorkflowClient();
  }

  @After
  public void tearDown() {
    testEnv.close();
  }

  @Test
  public void testNoCredit() {
    LoyaltyProgramActions activities = mock(LoyaltyProgramActions.class);
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    WorkflowOptions options =
        new WorkflowOptions.Builder()
            .setWorkflowId(customerId)
            .setExecutionStartToCloseTimeout(Duration.ofDays(10 * 365))
            .setTaskList(TASK_LIST)
            .build();
    LoyaltyProgramWorkflow workflow =
        workflowClient.newWorkflowStub(LoyaltyProgramWorkflow.class, options);

    WorkflowClient.start(workflow::loyaltyProgram, customerId);
    testEnv.sleep(Duration.ofDays(61));
    // As no trips were reported deactivates
    verify(activities, times(2)).sendMessage(customerId, "No credit this month! Buy something!");
  }

  @Test
  public void testActivate() {
    String customerId = "driver1";
    LoyaltyProgramActions activities = mock(LoyaltyProgramActions.class);
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    WorkflowOptions options =
        new WorkflowOptions.Builder()
            .setWorkflowId(customerId)
            .setExecutionStartToCloseTimeout(Duration.ofDays(10 * 365))
            .setTaskList(TASK_LIST)
            .build();
    LoyaltyProgramWorkflow workflow =
        workflowClient.newWorkflowStub(LoyaltyProgramWorkflow.class, options);

    WorkflowClient.start(workflow::loyaltyProgram, customerId);
    testEnv.sleep(Duration.ofHours(1));
    for (int i = 0; i < 30; i++) {
      testEnv.sleep(Duration.ofMinutes(30));
      String orderId = "order-" + i;
      workflow.onOrder(orderId);
    }
    assertEquals(30, workflow.getTotalOrderCount());
    testEnv.sleep(Duration.ofDays(32));

    verify(activities, times(1)).sendMessage(customerId, "Upgraded to the next tier: 1");
    verify(activities, times(1)).sendMessage(customerId, "Upgraded to the next tier: 2");
    verify(activities, times(1)).sendMessage(customerId, "Upgraded to the next tier: 3");
    verify(activities, times(1)).sendMessage(customerId, "Credited $5");
  }
}
