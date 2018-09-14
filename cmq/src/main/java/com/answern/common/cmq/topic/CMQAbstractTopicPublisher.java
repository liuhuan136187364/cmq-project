//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.answern.common.cmq.topic;

import com.answern.common.cmq.CMQInvoker;
import com.answern.common.cmq.config.CMQConfig;
import com.answern.common.cmq.exception.CMQClientException;
import com.answern.common.cmq.queue.QueueMeta;
import com.answern.common.cmq.utils.Assert;

public abstract class CMQAbstractTopicPublisher extends CMQInvoker {
    private static final String MODEL_NAME = "topic";

    @Override
    public String getModel() {
        return MODEL_NAME;
    }
}
