package com.uber.cadence.samples.driverrewards;

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

public class DriverRewardsWorkflowTest {

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
