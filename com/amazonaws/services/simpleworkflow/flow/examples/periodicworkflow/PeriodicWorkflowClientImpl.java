/*
 * This code was generated by AWS Flow Framework Annotation Processor.
 * Refer to Amazon Simple Workflow Service documentation at http://aws.amazon.com/documentation/swf 
 *
 * Any changes made directly to this file will be lost when 
 * the code is regenerated.
 */
 package com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow;

import com.amazonaws.services.simpleworkflow.flow.DataConverter;
import com.amazonaws.services.simpleworkflow.flow.StartWorkflowOptions;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClientBase;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.generic.GenericWorkflowClient;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.amazonaws.services.simpleworkflow.model.WorkflowType;

class PeriodicWorkflowClientImpl extends WorkflowClientBase implements PeriodicWorkflowClient {

    public PeriodicWorkflowClientImpl(WorkflowExecution workflowExecution, WorkflowType workflowType,  
            StartWorkflowOptions options, DataConverter dataConverter, GenericWorkflowClient genericClient) {
        super(workflowExecution, workflowType, options, dataConverter, genericClient);
    }
    
    @Override
    public final Promise<Void> startPeriodicWorkflow(com.amazonaws.services.simpleworkflow.model.ActivityType activity, Object[] activityArguments, com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.PeriodicWorkflowOptions options) { 
        return startPeriodicWorkflow(Promise.asPromise(activity), Promise.asPromise(activityArguments), Promise.asPromise(options), (StartWorkflowOptions)null);
    }
    
    @Override
    public final Promise<Void> startPeriodicWorkflow(com.amazonaws.services.simpleworkflow.model.ActivityType activity, Object[] activityArguments, com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.PeriodicWorkflowOptions options, Promise<?>... waitFor) {
        return startPeriodicWorkflow(Promise.asPromise(activity), Promise.asPromise(activityArguments), Promise.asPromise(options), (StartWorkflowOptions)null, waitFor);
    }
    
    
    @Override
    
    public final Promise<Void> startPeriodicWorkflow(com.amazonaws.services.simpleworkflow.model.ActivityType activity, Object[] activityArguments, com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.PeriodicWorkflowOptions options, StartWorkflowOptions optionsOverride, Promise<?>... waitFor) {
        return startPeriodicWorkflow(Promise.asPromise(activity), Promise.asPromise(activityArguments), Promise.asPromise(options), optionsOverride, waitFor);
    }

    @Override
    public final Promise<Void> startPeriodicWorkflow(Promise<com.amazonaws.services.simpleworkflow.model.ActivityType> activity, Promise<Object[]> activityArguments, Promise<com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.PeriodicWorkflowOptions> options) {
        return startPeriodicWorkflow(activity, activityArguments, options, (StartWorkflowOptions)null);
    }

    @Override
    public final Promise<Void> startPeriodicWorkflow(Promise<com.amazonaws.services.simpleworkflow.model.ActivityType> activity, Promise<Object[]> activityArguments, Promise<com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.PeriodicWorkflowOptions> options, Promise<?>... waitFor) {
        return startPeriodicWorkflow(activity, activityArguments, options, (StartWorkflowOptions)null, waitFor);
    }

    @Override
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Promise<Void> startPeriodicWorkflow(Promise<com.amazonaws.services.simpleworkflow.model.ActivityType> activity, Promise<Object[]> activityArguments, Promise<com.amazonaws.services.simpleworkflow.flow.examples.periodicworkflow.PeriodicWorkflowOptions> options, StartWorkflowOptions optionsOverride, Promise<?>... waitFor) {
        Promise[] _input_ = new Promise[3];
        _input_[0] = activity;
        _input_[1] = activityArguments;
        _input_[2] = options;
        return (Promise) startWorkflowExecution(_input_, optionsOverride, Void.class, waitFor);
    }
    	

}