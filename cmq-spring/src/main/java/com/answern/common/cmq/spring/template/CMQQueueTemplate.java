package com.answern.common.cmq.spring.template;

import com.answern.common.cmq.queue.CMQQueueProducer;

import java.util.List;

public class CMQQueueTemplate implements CMQTemplate {

    private CMQQueueProducer producer;

    public String sendMessage(String queueName,String message) {
        try {
            return producer.sendMessage(queueName,message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String sendMessage(String queueName,String message, int delayTime) {
        try {
            return producer.sendMessage(queueName,message, delayTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> batchSendMessage(String queueName, List<String> vtMsgBody) {
        try {
            return producer.batchSendMessage(queueName,vtMsgBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> batchSendMessage(String queueName,List<String> vtMsgBody, int delayTime) {
        try {
            return producer.batchSendMessage(queueName,vtMsgBody, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CMQQueueProducer getProducer() {
        return producer;
    }

    public void setProducer(CMQQueueProducer producer) {
        this.producer = producer;
    }
}
