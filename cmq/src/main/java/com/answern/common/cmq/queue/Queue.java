package com.answern.common.cmq.queue;

import java.util.*;
import java.lang.Integer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.answern.common.cmq.CMQClient;
import com.answern.common.cmq.exception.CMQClientException;
import com.answern.common.cmq.exception.CMQServerException;

/**
 * Queue class.
 *
 * @author York.
 *         Created 2016年9月26日.
 */
public class Queue{
	protected String queueName;
	protected CMQClient client;
	
	
	public Queue(String queueName, CMQClient client){
		this.queueName = queueName;
		this.client = client;
	}
	
	/**
	 * 设置队列属性
	 *
	 * @param meta        队列属性参数
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
	public void setQueueAttributes(QueueMeta meta) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		
		if(meta.maxMsgHeapNum > 0)
			param.put("maxMsgHeapNum",Integer.toString(meta.maxMsgHeapNum));
		if(meta.pollingWaitSeconds > 0)
			param.put("pollingWaitSeconds",Integer.toString(meta.pollingWaitSeconds));
		if(meta.visibilityTimeout > 0)
			param.put("visibilityTimeout",Integer.toString(meta.visibilityTimeout));
		if(meta.maxMsgSize > 0)
			param.put("maxMsgSize",Integer.toString(meta.maxMsgSize));
		if(meta.msgRetentionSeconds > 0)
			param.put("msgRetentionSeconds",Integer.toString(meta.msgRetentionSeconds));
		if(meta.rewindSeconds > 0)
			param.put("rewindSeconds",Integer.toString(meta.rewindSeconds));
		
		String result = this.client.call("SetQueueAttributes", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
	}
	
	/**
	 * 获取队列属性
	 *
	 * @return        返回的队列属性参数
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
	public QueueMeta getQueueAttributes() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		String result = this.client.call("GetQueueAttributes", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));

		QueueMeta meta = new QueueMeta();
		meta.maxMsgHeapNum = jsonObj.getIntValue("maxMsgHeapNum");
		meta.pollingWaitSeconds = jsonObj.getIntValue("pollingWaitSeconds");
		meta.visibilityTimeout = jsonObj.getIntValue("visibilityTimeout");
		meta.maxMsgSize = jsonObj.getIntValue("maxMsgSize");
		meta.msgRetentionSeconds = jsonObj.getIntValue("msgRetentionSeconds");
		meta.createTime = jsonObj.getIntValue("createTime");
		meta.lastModifyTime = jsonObj.getIntValue("lastModifyTime");
		meta.activeMsgNum = jsonObj.getIntValue("activeMsgNum");
		meta.inactiveMsgNum = jsonObj.getIntValue("inactiveMsgNum");
		meta.rewindmsgNum = jsonObj.getIntValue("rewindMsgNum");
		meta.minMsgTime = jsonObj.getIntValue("minMsgTime");
		meta.delayMsgNum = jsonObj.getIntValue("delayMsgNum");
		meta.rewindSeconds = jsonObj.getIntValue("rewindSeconds");
		
		
		return meta;
	}
	
	/**
	 * 发送消息
	 *
	 * @param msgBody     消息内容
	 * @return            服务器返回的消息唯一标识
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
    public String sendMessage(String msgBody) throws Exception {
        return sendMessage(msgBody, 0);
    }
	public String sendMessage(String msgBody ,int delayTime) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		param.put("msgBody",msgBody);
        param.put("delaySeconds",Integer.toString(delayTime));
		
		String result = this.client.call("SendMessage", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
		
		return jsonObj.getString("msgId");
	}
	
	/**
	 * 批量发送消息
	 *
	 * @param vtMsgBody   消息列表
	 * @return            服务器返回的消息唯一标识列表
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
    public List<String> batchSendMessage(List<String> vtMsgBody) throws Exception {
        return batchSendMessage(vtMsgBody, 0);
    }
	public List<String> batchSendMessage(List<String> vtMsgBody,int delayTime) throws Exception {

		if(vtMsgBody.isEmpty()  || vtMsgBody.size() > 16)
			throw new CMQClientException("Error: message size is empty or more than 16");
		
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		for(int i=0;i<vtMsgBody.size();i++)
		{
			String k = "msgBody." + Integer.toString(i+1);
			param.put(k,vtMsgBody.get(i));
		}
		param.put("delaySeconds", Integer.toString(delayTime));

		String result = this.client.call("BatchSendMessage", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
		
		ArrayList<String> vtMsgId = new ArrayList<String>();
		JSONArray jsonArray = jsonObj.getJSONArray("msgList");
		for(int i=0;i<jsonArray.size();i++)
		{	
			JSONObject obj = (JSONObject)jsonArray.get(i);
			vtMsgId.add(obj.getString("msgId"));
		}
		
		return vtMsgId;
	}
	
	/**
	 * 获取消息
	 *
	 * @param pollingWaitSeconds     请求最长的Polling等待时间
	 * @return 						 服务器返回消息
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
	public Message receiveMessage(int pollingWaitSeconds) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

	    param.put("queueName",this.queueName);
        if(pollingWaitSeconds >=  0)
        {
		param.put("UserpollingWaitSeconds",Integer.toString(pollingWaitSeconds *1000));
		param.put("pollingWaitSeconds", Integer.toString(pollingWaitSeconds) );
        }
        else
        {
		param.put("UserpollingWaitSeconds",Integer.toString(30000));
        }
		
		String result = this.client.call("ReceiveMessage", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
		
		Message msg = new Message();
		msg.msgId = jsonObj.getString("msgId");
		msg.receiptHandle = jsonObj.getString("receiptHandle");
		msg.msgBody = jsonObj.getString("msgBody");
		msg.enqueueTime = jsonObj.getLong("enqueueTime");
		msg.nextVisibleTime = jsonObj.getLong("nextVisibleTime");
		msg.firstDequeueTime = jsonObj.getLong("firstDequeueTime");
		msg.dequeueCount = jsonObj.getIntValue("dequeueCount");
		
		return msg;
	}
	
	/**
	 * 批量获取消息
	 *
	 * @param numOfMsg               准备获取消息数
	 * @param pollingWaitSeconds     请求最长的Polling等待时间
	 * @return                       服务器返回消息列表
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
	public List<Message> batchReceiveMessage(int numOfMsg, int pollingWaitSeconds) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		param.put("numOfMsg",Integer.toString(numOfMsg));
		if(pollingWaitSeconds >= 0)
        {
		param.put("UserpollingWaitSeconds",Integer.toString(pollingWaitSeconds*1000));
		param.put("pollingWaitSeconds", Integer.toString(pollingWaitSeconds) );
        }
        else
        {
		param.put("UserpollingWaitSeconds",Integer.toString(30000));
        }
		String result = this.client.call("BatchReceiveMessage", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
		
		ArrayList<Message> vtMessage = new ArrayList<Message>();
		
		JSONArray jsonArray = jsonObj.getJSONArray("msgInfoList");
		for(int i=0;i<jsonArray.size();i++)
		{	
			JSONObject obj = (JSONObject)jsonArray.get(i);
			Message msg = new Message();
			msg.msgId = obj.getString("msgId");
			msg.receiptHandle = obj.getString("receiptHandle");
			msg.msgBody = obj.getString("msgBody");
			msg.enqueueTime = obj.getLong("enqueueTime");
			msg.nextVisibleTime = obj.getLong("nextVisibleTime");
			msg.firstDequeueTime = obj.getLong("firstDequeueTime");
			msg.dequeueCount = obj.getIntValue("dequeueCount");
			
			vtMessage.add(msg);
		}
		
		return vtMessage;
	}
	
	/**
	 * 删除消息
	 *
	 * @param receiptHandle     消息句柄,获取消息时由服务器返回
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
	public void deleteMessage(String receiptHandle) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		param.put("receiptHandle",receiptHandle);
		
		String result = this.client.call("DeleteMessage", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
	}
	
	/**
	 * 批量删除消息
	 *
	 * @throws CMQClientException
	 * @throws CMQServerException
	 */
	public void batchDeleteMessage(List<String> vtReceiptHandle) throws Exception {
		if(vtReceiptHandle.isEmpty())
			return;
		
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		for(int i=0;i<vtReceiptHandle.size();i++)
		{
			String k = "receiptHandle." + Integer.toString(i+1);
			param.put(k,vtReceiptHandle.get(i));
		}
		
		String result = this.client.call("BatchDeleteMessage", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
	}
	
	
	/**
	 * 回溯队列
	 * @param backTrackingTime
	 * @throws CMQClientException
	 * @throws CMQServerException
	*/
	
	public void rewindQueue(int backTrackingTime) throws Exception {
		if(backTrackingTime  <=0 )
			return;
		
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("queueName",this.queueName);
		param.put("startConsumeTime",Integer.toString(backTrackingTime));
		
		String result = this.client.call("RewindQueue", param);
		JSONObject jsonObj = JSONObject.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
	}
	
}
