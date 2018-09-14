package com.answern.common.cmq.spring.registrar;

import com.answern.common.cmq.config.CMQConfig;
import com.answern.common.cmq.queue.CMQQueueConsumer;
import com.answern.common.cmq.queue.Message;
import com.answern.common.cmq.spring.annotation.QueueConsumer;
import com.answern.common.cmq.utils.Assert;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

public class QueueConsumerBean<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Method method;

    private QueueConsumer queueConsumer;

    private CMQQueueConsumer consumer;

    private CMQConfig config;

    private ExecutorService executor;

    public void afterSet(){
        Assert.notNull(method);
        Assert.notNull(executor);
        Assert.notNull(config);
        consumer = new CMQQueueConsumer() {

            @Override
            public boolean consume(Message message) {
                try {
                    method.invoke(QueueConsumerBean.this,message);
                    return true;
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                return false;
            }
        };
        String queueName = queueConsumer.queueName();
        if(StringUtils.isEmpty(queueName)){
            queueName = queueConsumer.value();
        }
        consumer.setQueueName(queueName);
        consumer.setConfig(config);
        consumer.setExecutor(executor);
        consumer.setPollingIntervalSeconds(queueConsumer.pollingIntervalSeconds());
        consumer.setPollingWaitSeconds(queueConsumer.pollingWaitSeconds());
        consumer.afterSet();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public QueueConsumer getQueueConsumer() {
        return queueConsumer;
    }

    public void setQueueConsumer(QueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
    }

    public CMQQueueConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(CMQQueueConsumer consumer) {
        this.consumer = consumer;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public CMQConfig getConfig() {
        return config;
    }

    public void setConfig(CMQConfig config) {
        this.config = config;
    }
}
