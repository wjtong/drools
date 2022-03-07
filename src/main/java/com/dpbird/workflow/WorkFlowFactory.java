package com.dpbird.workflow;

import com.dpbird.drools.DefaultWorkFlow;
import org.apache.ofbiz.base.lang.Factory;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.LocalDispatcher;

public class WorkFlowFactory implements Factory<WorkFlow, String> {
    private LocalDispatcher dispatcher;
    private Delegator delegator;

    public WorkFlowFactory(Delegator delegator) {
        this.delegator = delegator;
    }

    @Override
    public WorkFlow getInstance(String workEffortId) {
        // 需要从模块的配置文件中获取class
        return new DefaultWorkFlow(delegator, workEffortId);
    }
}
