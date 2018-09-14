package com.answern.common.cmq.topic;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.answern.common.cmq.CMQClient;
import com.answern.common.cmq.exception.CMQServerException;

/**
 * TODO subscription class.
 *
 * @author York.
 *         Created 2016年9月27日.
 */
public class Subscription {
	protected String topicName;
	protected String subscriptionName;
	protected CMQClient client;
	/**
	 * TODO construct .
	 *
	 * @param topicName
	 * @param subscriptionName
	 * @param client
	 */
	public Subscription(final String topicName, final String subscriptionName, CMQClient client)
	{
		this.topicName = topicName;
		this.subscriptionName = subscriptionName;
		this.client = client;	
	}
	public void ClearFilterTags() throws Exception
    {
    	Map<String, Object> param = new HashMap<String, Object>();

		param.put("topicName",this.topicName);
		param.put("subscriptionName", this.subscriptionName);
		String result = this.client.call("ClearSUbscriptionFIlterTags",param);
		
		JSONObject jsonObj = JSON.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
    
    }
	/**
	 * TODO set subscription attributes.
	 *
	 * @param meta SubscriptionMeata object
	 * @throws Exception
	 */
	public void SetSubscriptionAttributes(SubscriptionMeta meta) throws Exception
	{
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("topicName",this.topicName);
		param.put("subscriptionName", this.subscriptionName);
		if( !meta.NotifyStrategy.equals(""))
			param.put("notifyStrategy",meta.NotifyStrategy);
		if( !meta.NotifyContentFormat.equals(""))
			param.put("notifyContentFormat", meta.NotifyContentFormat);
		if( meta.FilterTag != null )
		{
			int n = 1 ;
			for(String flag : meta.FilterTag)
			{
				param.put("filterTag."+Integer.toString(n), flag);
				++n;
			}
		}
		if( meta.bindingKey != null )
		{
			int n = 1 ;
			for(String flag : meta.bindingKey)
			{
				param.put("bindingKey."+Integer.toString(n), flag);
				++n;
			}
		}
	
		String result = this.client.call("SetSubscriptionAttributes", param);
		
		JSONObject jsonObj = JSON.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));
	}
	
	/**
	 * TODO get subscription attributes.
	 *
	 * @return  subscription meta object
	 * @throws Exception
	 */
	public SubscriptionMeta getSubscriptionAttributes() throws Exception
	{
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("topicName",this.topicName);
		param.put("subscriptionName", this.subscriptionName);
		
		String result = this.client.call("GetSubscriptionAttributes", param);
		JSONObject jsonObj = JSON.parseObject(result);
		int code = jsonObj.getIntValue("code");
		if(code != 0)
			throw new CMQServerException(code,jsonObj.getString("message"),jsonObj.getString("requestId"));

		SubscriptionMeta meta = new SubscriptionMeta();
		meta.FilterTag = new Vector<String>();
        if(jsonObj.containsKey("endpoint"))
		    meta.Endpoint = jsonObj.getString("endpoint");
        if(jsonObj.containsKey("notifyStrategy"))
		    meta.NotifyStrategy = jsonObj.getString("notifyStrategy");
        if(jsonObj.containsKey("notifyContentFormat"))
		    meta.NotifyContentFormat = jsonObj.getString("notifyContentFormat");
        if(jsonObj.containsKey("protocol"))
		    meta.Protocal = jsonObj.getString("protocol");
        if(jsonObj.containsKey("createTime"))
		    meta.CreateTime = jsonObj.getIntValue("createTime");
        if(jsonObj.containsKey("lastModifyTime"))
		    meta.LastModifyTime = jsonObj.getIntValue("lastModifyTime");
        if(jsonObj.containsKey("msgCount"))
		   meta.msgCount = jsonObj.getIntValue("msgCount");
	    if(jsonObj.containsKey("filterTag"))
        {
		    JSONArray jsonArray = jsonObj.getJSONArray("filterTag");
	 	    for(int i=0;i<jsonArray.size();i++)
		    {	
			    JSONObject obj = (JSONObject)jsonArray.get(i);
			    meta.FilterTag.add(obj.toString());
	    	} 
        }
		if(jsonObj.containsKey("bindingKey"))
        {
		    JSONArray jsonArray = jsonObj.getJSONArray("bindingKey");
	 	    for(int i=0;i<jsonArray.size();i++)
		    {	
			    JSONObject obj = (JSONObject)jsonArray.get(i);
			    meta.bindingKey.add(obj.toString());
	    	} 
        }
	
		return meta;
	}
	
}
