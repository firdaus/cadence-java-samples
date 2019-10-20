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
import com.uber.cadence.samples.driverrewards.DriverRewardsActivities;
import com.uber.cadence.samples.driverrewards.DriverRewardsWorker;
import com.uber.cadence.samples.driverrewards.DriverRewardsWorkflow;
import com.uber.cadence.samples.driverrewards.DriverRewardsWorkflowImpl;
import com.uber.cadence.testing.TestWorkflowEnvironment;
import com.uber.cadence.worker.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LoyaltyProgramWorkflowTest {

  String driverId = "driver1";
  private TestWorkflowEnvironment testEnv;
  private Worker worker;
  private WorkflowClient workflowClient;

  @Before
  public void setUp() {
    testEnv = TestWorkflowEnvironment.newInstance();
    worker = testEnv.newWorker(DriverRewardsWorker.TASK_LIST);
    worker.registerWorkflowImplementationTypes(DriverRewardsWorkflowImpl.class);
    workflowClient = testEnv.newWorkflowClient();
  }

  @After
  public void tearDown() {
    testEnv.close();
  }

  @Test
  public void testDeactivate() {
    DriverRewardsActivities activities = mock(DriverRewardsActivities.class);
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    WorkflowOptions options = new WorkflowOptions.Builder().setWorkflowId(driverId).build();
    DriverRewardsWorkflow workflow =
        workflowClient.newWorkflowStub(DriverRewardsWorkflow.class, options);

    WorkflowClient.start(workflow::driverRewards, driverId);
    testEnv.sleep(Duration.ofDays(32));
    // As no trips were reported deactivates
    verify(activities, times(1)).deactivate(driverId);
  }

  @Test
  public void testActivate() {
    String driverId = "driver1";
    DriverRewardsActivities activities = mock(DriverRewardsActivities.class);
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    WorkflowOptions options = new WorkflowOptions.Builder().setWorkflowId(driverId).build();
    DriverRewardsWorkflow workflow =
        workflowClient.newWorkflowStub(DriverRewardsWorkflow.class, options);

    WorkflowClient.start(workflow::driverRewards, driverId);
    testEnv.sleep(Duration.ofHours(1));
    for (int i = 0; i < 30; i++) {
      testEnv.sleep(Duration.ofMinutes(30));
      workflow.onTrip(5);
    }
    assertEquals(5.0, workflow.getRating(), 0.01);
    testEnv.sleep(Duration.ofDays(32));
    //
    verify(activities, times(1)).activate(driverId);
  }
}
