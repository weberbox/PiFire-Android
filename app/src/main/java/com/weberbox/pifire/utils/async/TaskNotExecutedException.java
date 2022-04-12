package com.weberbox.pifire.utils.async;

public class TaskNotExecutedException extends Exception {
    public TaskNotExecutedException() {
        super("Task not executed before calling get()");
    }
}
