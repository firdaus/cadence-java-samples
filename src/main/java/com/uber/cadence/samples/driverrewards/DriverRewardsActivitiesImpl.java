package com.uber.cadence.samples.driverrewards;

public class DriverRewardsActivitiesImpl implements DriverRewardsActivities {

    @Override
    public void activate(String driverId) {
        System.out.println("Activated driver " + driverId);
    }

    @Override
    public void deactivate(String driverId) {
        System.out.println("Deactivated driver " + driverId);
    }

}
