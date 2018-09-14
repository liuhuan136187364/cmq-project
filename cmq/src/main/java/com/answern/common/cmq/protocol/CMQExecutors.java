package com.answern.common.cmq.protocol;

public class CMQExecutors {

    public static CMQExecutor create(String method){
        try {
            return (CMQExecutor) ExecutorMethod.valueOf(method.toUpperCase()).getExecutorClass().newInstance();
        } catch (Exception e){
            throw new RuntimeException("the method ["+method+"]  does not support.");
        }
    }
}
