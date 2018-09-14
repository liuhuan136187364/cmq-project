package com.answern.common.cmq.protocol;

import java.util.Map;

public abstract class CMQExecutor {

    protected static final String DEFAULT_CHARSET = "UTF-8";

    public abstract String execute(String url, Map<String,Object> params);
}
