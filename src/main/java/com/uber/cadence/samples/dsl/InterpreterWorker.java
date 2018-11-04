package com.uber.cadence.samples.dsl;

public class InterpreterWorker {
    InterpreterWorkflowImpl impl;
    SequenceInterpreter activity;

    InterpreterWorker() {
        System.out.println(impl.getCurrentActivity());
    }
}
