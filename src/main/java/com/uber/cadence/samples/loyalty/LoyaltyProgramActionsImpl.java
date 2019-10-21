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

public final class LoyaltyProgramActionsImpl implements LoyaltyProgramActions {

  @Override
  public void sendMessage(String customerId, String message) {
    System.out.println("Message to customer " + customerId + ": " + message);
  }

  @Override
  public void updateTier(String customerId, int tier) {
    System.out.println("Customer " + customerId + " updated to " + tier + " tier");
  }

  @Override
  public void credit(String customerId, int cents) {
    System.out.println("Customer " + customerId + " account credited " + cents + " cents");
  }
}
