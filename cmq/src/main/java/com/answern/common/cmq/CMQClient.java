package com.answern.common.cmq;

import com.answern.common.cmq.config.CMQConfig;
import com.answern.common.cmq.exception.CMQClientException;
import com.answern.common.cmq.protocol.CMQExecutor;
import com.answern.common.cmq.protocol.CMQExecutors;
import com.answern.common.cmq.utils.CMQTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class CMQClient {

	private static final Logger logger = LoggerFactory.getLogger(CMQClient.class);

	protected CMQConfig cmqConfig;

	private String endpoint;
	
	protected String CURRENT_VERSION = "SDK_JAVA_1.3";
	
	protected String signMethod;

	public CMQClient(CMQConfig cmqConfig, String model){
		this.signMethod="sha1";
		this.cmqConfig = cmqConfig;
		this.endpoint = cmqConfig.getEndpoint().replace("{{model}}",model);
	}
	
	public void setSignMethod(String signMethod)
	{
		if(signMethod == "sha1" || signMethod == "sha256")
		    this.signMethod = signMethod;
		else
		    throw new CMQClientException("Only support sha256 or sha1");
	}
	public String call(String action, Map<String,Object> param) throws Exception{
		String rsp = "";
		param.put("Action", action);
		param.put("Nonce", Integer.toString(new Random().nextInt(Integer.MAX_VALUE)));
		param.put("SecretId", cmqConfig.getSecretId());
		param.put("Timestamp", Long.toString(System.currentTimeMillis() / 1000));
		param.put("RequestClient", this.CURRENT_VERSION);
		if (this.signMethod =="sha256"){
			param.put("SignatureMethod","HmacSHA256");
		}else{
			param.put("SignatureMethod","HmacSHA1");
		}
		param.put("Signature",sign(param));
		String url = endpoint+cmqConfig.getPath();
		CMQExecutor executor = CMQExecutors.create(cmqConfig.getMethod());
		rsp = executor.execute(url,param);
		return rsp;
	}

	private String sign(Map<String,Object> params) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String host="";
        if(endpoint.startsWith("https"))
            host = endpoint.substring(8);
        else
            host = endpoint.substring(7);
        String src = "";
        src += cmqConfig.getMethod() + host + cmqConfig.getPath() + "?";

        boolean flag = false;
        String[] keys = params.keySet().stream().toArray(String[]::new);
        Arrays.sort(keys);
        for(String key : keys){
            if(flag)
                src += "&";
            //src += key + "=" + param.get(key);
            src += key.replace("_", ".") + "=" + params.get(key);
            flag = true;
        }
        return CMQTool.sign(src, cmqConfig.getSecretKey(),this.signMethod);
    }

}
