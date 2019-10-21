package com.uber.cadence.samples.loyalty;

interface LoyaltyProgramAction {
  void notify(String customerId, String message);

  void updateTier(String customerId, int tier);

  void credit(String customerId, int cents);
}
