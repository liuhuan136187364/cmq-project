//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.answern.common.cmq.queue;

import com.answern.common.cmq.CMQInvoker;
import com.answern.common.cmq.exception.CMQClientException;

import java.util.concurrent.ExecutorService;

public abstract class CMQAbstractQueueConsumer extends CMQInvoker {

    private static final String MODEL_NAME = "queue";
    public ExecutorService executor;
    public QueueMeta queueMeta;
    public String queueName;
    public int pollingWaitSeconds = 10;
    public int pollingIntervalSeconds = 1;

    public CMQAbstractQueueConsumer() {
        super();
    }

    public int getPollingWaitSeconds() {
        return this.pollingWaitSeconds;
    }

    public void setPollingWaitSeconds(int pollingWaitSeconds) {
        this.pollingWaitSeconds = pollingWaitSeconds;
    }

    public QueueMeta getQueueMeta() {
        return this.queueMeta;
    }

    public void setQueueMeta(QueueMeta queueMeta) {
        this.queueMeta = queueMeta;
    }

    public String getQueueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getPollingIntervalSeconds() {
        return this.pollingIntervalSeconds;
    }

    public void setPollingIntervalSeconds(int pollingIntervalSeconds) {
        this.pollingIntervalSeconds = pollingIntervalSeconds;
    }

    public void afterSet() {
        super.afterSet();
        if (this.getQueueName() != null && this.getQueueName().length() != 0) {
            executor.submit((new CMQAbstractQueueConsumer.ReceivedThread(this)));
        } else {
            throw new CMQClientException("CMQQueueConsumer queue init error.");
        }
    }

    abstract void receiveMq();

    final class ReceivedThread extends Thread {
        CMQAbstractQueueConsumer consumer;

        public ReceivedThread(CMQAbstractQueueConsumer consumer) {
            this.consumer = consumer;
        }

        public void run() {
            this.consumer.receiveMq();
        }
    }

    @Override
    public String getModel() {
        return MODEL_NAME;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}
