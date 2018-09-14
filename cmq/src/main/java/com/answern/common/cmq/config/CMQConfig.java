package com.answern.common.cmq.config;

import java.io.Serializable;

public class CMQConfig implements Serializable {
    private String endpoint;
    private String path;
    private String secretId;
    private String secretKey;
    private String method;

    public CMQConfig() {
    }

    public CMQConfig(String endpoint, String path, String secretId, String secretKey, String method) {
        this.endpoint = endpoint;
        this.path = path;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.method = method;
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
