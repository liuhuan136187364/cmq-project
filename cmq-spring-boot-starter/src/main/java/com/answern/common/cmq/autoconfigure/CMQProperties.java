package com.answern.common.cmq.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = CMQProperties.CMQ_PREFIX)
@ConditionalOnProperty(prefix = "cmq", value = "enable", matchIfMissing = true)
public class CMQProperties {
    protected static final String CMQ_PREFIX = "cmq";

    private String endpoint;
    private String path;
    private String secretId;
    private String secretKey;
    private String method;

    public static String getCmqPrefix() {
        return CMQ_PREFIX;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
