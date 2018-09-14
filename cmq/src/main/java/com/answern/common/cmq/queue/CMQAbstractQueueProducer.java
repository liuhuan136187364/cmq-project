//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.answern.common.cmq.queue;

import com.answern.common.cmq.CMQInvoker;
import com.answern.common.cmq.exception.CMQClientException;

public abstract class CMQAbstractQueueProducer extends CMQInvoker {
    private static final String MODEL_NAME = "queue";

    @Override
    public String getModel() {
        return MODEL_NAME;
    }
}
