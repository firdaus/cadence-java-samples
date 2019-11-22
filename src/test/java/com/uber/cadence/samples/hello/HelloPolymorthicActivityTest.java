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

package com.uber.cadence.samples.hello;

import static org.junit.Assert.*;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.samples.hello.HelloPolymorthicActivity.GreetingWorkflow;
import com.uber.cadence.samples.hello.HelloPolymorthicActivity.GreetingWorkflowImpl;
import com.uber.cadence.samples.hello.HelloPolymorthicActivity.PolymorphicActivitiesImpl;
import com.uber.cadence.testing.TestWorkflowEnvironment;
import com.uber.cadence.worker.Worker;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Unit test for {@link HelloPolymorthicActivity}. Doesn't use an external Cadence service. */
public class HelloPolymorthicActivityTest {

  private TestWorkflowEnvironment testEnv;
  private Worker worker;
  private WorkflowClient workflowClient;

  @Before
  public void setUp() {
    testEnv = TestWorkflowEnvironment.newInstance();
    worker = testEnv.newWorker(HelloPolymorthicActivity.TASK_LIST);
    worker.registerWorkflowImplementationTypes(GreetingWorkflowImpl.class);

    workflowClient = testEnv.newWorkflowClient();
  }

  @After
  public void tearDown() {
    testEnv.close();
  }

  @Test
  public void testActivityImpl() {
    Map<String, Object> activities = new HashMap<>();
    activities.put("hello", new HelloPolymorthicActivity.GreetingHello());
    activities.put("bye", new HelloPolymorthicActivity.GreetingBye());
    worker.registerActivitiesImplementations(new PolymorphicActivitiesImpl(activities));
    testEnv.start();

    // Get a workflow stub using the same task list the worker uses.
    GreetingWorkflow workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class);
    // Execute a workflow waiting for it to complete.
    String greeting = workflow.getGreeting("World");
    assertEquals("Hello World!\nBye World!\n", greeting);
  }
}
