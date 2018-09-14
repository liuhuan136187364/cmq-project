package com.answern.common.cmq.protocol;

public enum ExecutorMethod {
    GET(CMQGetExecutor.class),POST(CMQPostExecutor.class);

    private Class executorClass;

    private ExecutorMethod(Class executorClass) {
        this.executorClass = executorClass;
    }

    public Class getExecutorClass() {
        return executorClass;
    }
}
