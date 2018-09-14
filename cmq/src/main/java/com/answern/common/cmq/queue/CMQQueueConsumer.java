package com.answern.common.cmq.queue;

import com.answern.common.cmq.exception.CMQClientException;
import com.answern.common.cmq.exception.CMQServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CMQQueueConsumer extends CMQAbstractQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CMQQueueConsumer.class);
    
    public abstract boolean consume(Message msg);

    public void receiveMq() {
        Queue queue;
        try {
            queue = this.getQueue(this.getQueueName());
        } catch (Exception e) {
            throw new CMQClientException("CMQQueueConsumer queue init error.");
        }

        while(true) {

            try {
                Message msg = queue.receiveMessage(this.getPollingWaitSeconds());
                this.consume(msg, queue);
            } catch (Throwable var6) {
                if (var6 instanceof CMQServerException) {
                    CMQServerException serverException = (CMQServerException)var6;
                    if (serverException.getErrorCode() == 7000) {
                        logger.error(serverException.getErrorMessage());
                    } else {
                        logger.error(serverException.getErrorMessage());
                    }
                } else {
                    logger.error(var6.getMessage());
                }

                try {
                    Thread.sleep((long)this.getPollingIntervalSeconds()*1000);
                } catch (InterruptedException var4) {
                    logger.error(var4.getMessage());
                }
            }
        }
    }

    public void consume(Message message, Queue queue) {
        boolean consumeResult = false;

        try {
            consumeResult = this.consume(message);
        } catch (Throwable var6) {
            logger.error("[cmq] consume message fail ,message.receiptHandle=" + message.receiptHandle);
            return;
        }

        if (consumeResult) {
            try {
                queue.deleteMessage(message.receiptHandle);
            } catch (Throwable var5) {
                logger.error("[cmq] delete consumed message fail ,message.receiptHandle=" + message.receiptHandle);
            }
        }

    }

    final class ConsumerThread implements Runnable {
        Message message;
        Queue queue;

        public ConsumerThread(Message message, Queue queue) {
            this.message = message;
            this.queue = queue;
        }

        public void run() {
            CMQQueueConsumer.this.consume(this.message, this.queue);
        }
    }
}
