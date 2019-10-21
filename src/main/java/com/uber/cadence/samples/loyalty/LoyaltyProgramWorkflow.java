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

import com.uber.cadence.workflow.QueryMethod;
import com.uber.cadence.workflow.SignalMethod;
import com.uber.cadence.workflow.WorkflowMethod;
import java.util.List;

/** Loyalty program workflow interface. */
public interface LoyaltyProgramWorkflow {

  /**
   * Main workflow method called when workflow is started.
   *
   * @param customerId unique customer id.
   */
  @WorkflowMethod
  void loyaltyProgram(String customerId, List<String> orderIds);

  /**
   * Called upon each customer order.
   *
   * @param orderId of the placed order
   */
  @SignalMethod
  void onOrder(String orderId);

  /** Returns number orders placed this month. */
  @QueryMethod
  int getThisMonthOrderCount();

  /** Returns total number orders placed by the customer. */
  @QueryMethod
  int getTotalOrderCount();
}
