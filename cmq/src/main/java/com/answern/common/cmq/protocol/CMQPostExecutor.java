package com.answern.common.cmq.protocol;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CMQPostExecutor extends CMQExecutor{
    private static final Logger logger = LoggerFactory.getLogger(CMQGetExecutor.class);

    public String execute(String url, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        try {
            HttpEntity urlEncodedFormEntity = buildEntity(params);
            httpPost.setEntity(urlEncodedFormEntity);
            CloseableHttpResponse response = CMQHttpClient.getInstance().execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            }else{
                logger.error("CMQ api execute faild ,http code is ["+statusCode+"]");
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private UrlEncodedFormEntity buildEntity(Map<String, Object> params) throws UnsupportedEncodingException {
        List<NameValuePair> paramList = new ArrayList<>();
        params.forEach((k,v) -> paramList.add(new BasicNameValuePair(k,String.valueOf(v))));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramList,DEFAULT_CHARSET);
        return urlEncodedFormEntity;
    }
}
