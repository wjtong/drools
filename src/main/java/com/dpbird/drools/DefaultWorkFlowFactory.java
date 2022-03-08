package com.dpbird.drools;

import com.dpbird.workflow.WorkFlow;
import com.dpbird.workflow.WorkFlowFactory;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.LocalDispatcher;

public final class DefaultWorkFlowFactory implements WorkFlowFactory {
    private LocalDispatcher dispatcher;
    private Delegator delegator;

    @Override
    public WorkFlow getInstance(String workEffortId) {
        // 需要从模块的配置文件中获取class
        return new DefaultWorkFlow(delegator, workEffortId);
    }

    @Override
    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
    }
}
