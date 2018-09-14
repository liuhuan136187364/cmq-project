//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.answern.common.cmq.queue;

import java.util.List;

public class CMQQueueProducer extends CMQAbstractQueueProducer {
    
    public String sendMessage(String queueName,String message) throws Exception {
        Queue queue = this.getQueue(queueName);
        return queue.sendMessage(message);
    }

    public String sendMessage(String queueName,String message, int delayTime) throws Exception {
        Queue queue = this.getQueue(queueName);
        return queue.sendMessage(message, delayTime);
    }

    public List<String> batchSendMessage(String queueName,List<String> vtMsgBody) throws Exception {
        Queue queue = this.getQueue(queueName);
        return queue.batchSendMessage(vtMsgBody);
    }

    public List<String> batchSendMessage(String queueName,List<String> vtMsgBody, int delayTime) throws Exception {
        Queue queue = this.getQueue(queueName);
        return queue.batchSendMessage(vtMsgBody, delayTime);
    }
}
