package com.answern.common.cmq.spring.template;

import com.answern.common.cmq.topic.CMQTopicPublisher;

import java.util.List;
import java.util.Vector;

public class CMQTopicTemplate implements CMQTemplate {
    
    private CMQTopicPublisher publisher;

    public String publishMessage(String topicName,String message) {
        try {
            return publisher.publishMessage(topicName,message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String publishMessage(String topicName,String message, String routingKey){
        try {
            return publisher.publishMessage(topicName,message,routingKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String publishMessage(String topicName,String msg, List<String> vTagList, String routingKey){
        try {
            return publisher.publishMessage(topicName,msg,vTagList,routingKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Vector<String> batchPublishMessage(String topicName,List<String> vMsgList){
        try {
            return publisher.batchPublishMessage(topicName,vMsgList, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Vector<String> batchPublishMessage(String topicName,List<String> vMsgList, String routingKey){
        try {
            return publisher.batchPublishMessage(topicName,vMsgList, null, routingKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Vector<String> batchPublishMessage(String topicName,List<String> vMsgList, List<String> vTagList, String routingKey){
        try {
            return publisher.batchPublishMessage(topicName,vMsgList,vTagList,routingKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int ListSubscription(String topicName,final int offset, int limit,
                                final String searchWord, List<String> vSubscriptionList
    ){
        try {
            return publisher.ListSubscription(topicName,offset,limit,searchWord,vSubscriptionList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CMQTopicPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(CMQTopicPublisher publisher) {
        this.publisher = publisher;
    }
}
