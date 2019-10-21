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

import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.Worker.Factory;

/**
 * This is the process that hosts all workflows and activities in this sample. Run multiple
 * instances of the worker in different windows. Then start a workflow by running the
 * LoyaltyProgramStarter.
 */
public class LoyaltyProgramWorkflowWorker {

  static final String TASK_LIST = "LoyaltyProgram";

  public static void main(String[] args) {
    Factory factory = new Factory(DOMAIN);
    Worker worker = factory.newWorker(TASK_LIST);
    worker.registerWorkflowImplementationTypes(LoyaltyProgramWorkflowImpl.class);
    factory.start();
    System.out.println("WorkflowWorker started for task list: " + TASK_LIST);
  }
}
