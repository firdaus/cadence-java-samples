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

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.workflow.Workflow;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements loyalty program business logic as a workflow. Compare to a non fault tolerant
 * implementation.
 *
 * @see LoyaltyProgram
 */
public final class LoyaltyProgramWorkflowImpl implements LoyaltyProgramWorkflow {

  /** Stub used to invoke activities through a strongly typed interface. */
  private static final int ORDER_ID_MAX_SIZE = 1000;

  private final Set<String> orderIds = newLRUSet();

  private final ActivityOptions activityOptions =
      new ActivityOptions.Builder().setScheduleToCloseTimeout(Duration.ofHours(1)).build();

  private final LoyaltyProgramActions loyaltyProgram =
      Workflow.newActivityStub(LoyaltyProgramActions.class, activityOptions);

  private String customerId;
  private int ordersThisMonth;
  private int totalOrders;
  private int currentTier;

  /** New order notification */
  @Override
  public void onOrder(String orderId) {
    if (!orderIds.add(orderId)) {
      return;
    }
    ordersThisMonth++;
    totalOrders++;
    int tier = totalOrders / 10;
    if (tier > currentTier) {
      currentTier = tier;
      loyaltyProgram.sendMessage(customerId, "Upgraded to the next tier: " + currentTier);
      loyaltyProgram.updateTier(customerId, currentTier);
    }
  }

  @Override
  public int getThisMonthOrderCount() {
    return ordersThisMonth;
  }

  @Override
  public int getTotalOrderCount() {
    return totalOrders;
  }

  /** Called when customer signs up for the rewards program. */
  @Override
  public void loyaltyProgram(String customerId) {
    this.customerId = customerId;
    while (true) {
      ordersThisMonth = 0;
      Workflow.sleep(Duration.ofDays(30).toMillis());
      if (ordersThisMonth > 0) {
        loyaltyProgram.credit(customerId, 500);
        loyaltyProgram.sendMessage(customerId, "Credited $5");
      } else {
        loyaltyProgram.sendMessage(customerId, "No credit this month! Buy something!");
      }
    }
  }

  private Set<String> newLRUSet() {
    return Collections.newSetFromMap(
        new LinkedHashMap<String, Boolean>() {
          @Override
          protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return size() > ORDER_ID_MAX_SIZE;
          }
        });
  }
}
