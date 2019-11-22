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

import static com.uber.cadence.samples.common.SampleConstants.DOMAIN;

import com.uber.cadence.activity.Activity;
import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.internal.sync.WorkflowInternal;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.workflow.ActivityStub;
import com.uber.cadence.workflow.Workflow;
import com.uber.cadence.workflow.WorkflowMethod;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello World Cadence workflow that executes a single activity. Requires a local instance the
 * Cadence service to be running.
 */
public class HelloPolymorthicActivity {

  static final String TASK_LIST = "HelloActivity";

  /** Workflow interface has to have at least one method annotated with @WorkflowMethod. */
  public interface GreetingWorkflow {
    /** @return greeting string */
    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 10, taskList = TASK_LIST)
    String getGreeting(String name);
  }

  /** Activity interface is just a POJI. */
  public interface PolymorphicActivities {
    Object invoke(String discriminator, String method, Object[] args);

    @SuppressWarnings("unchecked")
    static <T> T newStub(Class<T> type, String discriminator) {
      return (T)
          Proxy.newProxyInstance(
              WorkflowInternal.class.getClassLoader(),
              new Class<?>[] {type},
              new PolymorphicActivitiesInvocationHandler(
                  discriminator,
                  new ActivityOptions.Builder()
                      .setScheduleToCloseTimeout(Duration.ofMinutes(1))
                      .build()));
    }
  }

  static class PolymorphicActivitiesInvocationHandler implements InvocationHandler {
    private final String discriminator;
    private final ActivityStub
        polymorphicActivities; // Using untyped stub to be able to deserialize any return type

    public PolymorphicActivitiesInvocationHandler(String discriminator, ActivityOptions options) {
      this.discriminator = discriminator;
      this.polymorphicActivities = Workflow.newUntypedActivityStub(options);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.equals(Object.class.getMethod("toString"))) {
        return "PolymorphicActivitiesInvocationHandler";
      }
      if (!method.getDeclaringClass().isInterface()) {
        throw new IllegalArgumentException(
            "Interface type is expected: " + method.getDeclaringClass());
      }
      return polymorphicActivities.execute(
          "PolymorphicActivities::invoke",
          method.getReturnType(),
          new Object[] {discriminator, method.getName(), args});
    }
  }

  static class PolymorphicActivitiesImpl implements PolymorphicActivities {
    private final Map<String, Object> activities;

    PolymorphicActivitiesImpl(Map<String, Object> activities) {
      this.activities = activities;
    }

    @Override
    public Object invoke(String discriminator, String method, Object[] args) {
      try {
        Object impl = activities.get(discriminator);
        Method[] methods = impl.getClass().getMethods();
        for (Method m : methods) {
          if (m.getName().equals(method)) {
            return m.invoke(impl, args);
          }
        }
        throw new RuntimeException("Unkwnown method " + method);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw Activity.wrap(e);
      }
    }
  }

  public interface GreetingActivities {
    String composeGreeting(String name);
  }

  public static class GreetingHello implements GreetingActivities {
    @Override
    public String composeGreeting(String name) {
      return "Hello " + name + "!";
    }
  }

  public static class GreetingBye implements GreetingActivities {
    @Override
    public String composeGreeting(String name) {
      return "Bye " + name + "!";
    }
  }
  /** GreetingWorkflow implementation that calls GreetingsActivities#composeGreeting. */
  public static class GreetingWorkflowImpl implements GreetingWorkflow {

    private final GreetingActivities[] greetings =
        new GreetingActivities[] {
          PolymorphicActivities.newStub(GreetingActivities.class, "hello"),
          PolymorphicActivities.newStub(GreetingActivities.class, "bye")
        };

    @Override
    public String getGreeting(String name) {
      String result = "";
      for (int i = 0; i < 2; i++) {
        result += greetings[i].composeGreeting("World") + "\n";
      }
      return result;
    }
  }

  public static void main(String[] args) {
    Worker.Factory factory = new Worker.Factory(DOMAIN);
    // Start a worker that hosts both workflow and activity implementations.
    Worker worker = factory.newWorker(TASK_LIST);
    // Workflows are stateful. So you need a type to create instances.
    worker.registerWorkflowImplementationTypes(GreetingWorkflowImpl.class);
    // Activities are stateless and thread safe. So a shared instance is used.
    Map<String, Object> activities = new HashMap<>();
    activities.put("hello", new GreetingHello());
    activities.put("bye", new GreetingBye());
    worker.registerActivitiesImplementations(new PolymorphicActivitiesImpl(activities));
    // Start listening to the workflow and activity task lists.
    factory.start();

    // Start a workflow execution. Usually this is done from another program.
    WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);
    // Get a workflow stub using the same task list the worker uses.
    GreetingWorkflow workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class);
    // Execute a workflow waiting for it to complete.
    String greeting = workflow.getGreeting("World");
    System.out.println(greeting);
    System.exit(0);
  }
}
