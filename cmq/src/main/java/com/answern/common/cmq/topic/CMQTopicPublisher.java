package com.answern.common.cmq.topic;

import java.util.List;
import java.util.Vector;

public class CMQTopicPublisher extends CMQAbstractTopicPublisher {
    /**
     * publish message without  tags.
     *
     * @param message String
     * @return msgId, String
     * @throws Exception
     */
    public String publishMessage(String topicName,String message) throws Exception {
        return getTopic(topicName).publishMessage(message);
    }

    public String publishMessage(String topicName,String message, String routingKey) throws Exception {
        return getTopic(topicName).publishMessage(message,routingKey);
    }

    /**
     * publish message .
     *
     * @param msg      String message body
     * @param vTagList Vector<String>  message tag
     * @return msgId String
     * @throws Exception
     */
    public String publishMessage(String topicName,String msg, List<String> vTagList, String routingKey) throws Exception {
        return getTopic(topicName).publishMessage(msg,vTagList,routingKey);
    }

    /**
     * TODO batch publish message without tags.
     *
     * @param vMsgList Vector<String> message array
     * @return msgId Vector<String> message id array
     * @throws Exception
     */
    public Vector<String> batchPublishMessage(String topicName,List<String> vMsgList) throws Exception {
        return getTopic(topicName).batchPublishMessage(vMsgList, null, null);
    }

    public Vector<String> batchPublishMessage(String topicName,List<String> vMsgList, String routingKey) throws Exception {
        return getTopic(topicName).batchPublishMessage(vMsgList, null, routingKey);
    }

    /**
     * batch publish message
     *
     * @param vMsgList message array
     * @param vTagList message tag array
     * @return message handles array
     * @throws Exception
     */
    public Vector<String> batchPublishMessage(String topicName,List<String> vMsgList, List<String> vTagList, String routingKey) throws Exception {
        return getTopic(topicName).batchPublishMessage(vMsgList,vTagList,routingKey);
    }

    /**
     * TODO list subscription by topic.
     *
     * @param offset            int
     * @param limit             int
     * @param searchWord        String
     * @param vSubscriptionList List<String>
     * @return totalCount          int
     * @throws Exception
     */
    public int ListSubscription(String topicName,final int offset, int limit,
                                final String searchWord, List<String> vSubscriptionList
    ) throws Exception {
        return getTopic(topicName).ListSubscription(offset,limit,searchWord,vSubscriptionList);
    }
}
