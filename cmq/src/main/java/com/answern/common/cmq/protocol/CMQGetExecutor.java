package com.answern.common.cmq.protocol;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Map;

public class CMQGetExecutor extends CMQExecutor{

    private static final Logger logger = LoggerFactory.getLogger(CMQGetExecutor.class);

    public String execute(String url, Map<String, Object> params) {
        HttpGet httpGet = new HttpGet();
        try {
            URIBuilder uriBuilder = buildURI(url,params);
            httpGet.setURI(uriBuilder.build());
            CloseableHttpResponse response = CMQHttpClient.getInstance().execute(httpGet);
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

    private URIBuilder buildURI(String url, Map<String, Object> params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        params.forEach((k,v) -> builder.setParameter(k, String.valueOf(v)));
        return builder;
    }
}
