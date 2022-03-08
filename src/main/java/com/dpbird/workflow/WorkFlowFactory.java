package com.dpbird.workflow;

import com.dpbird.drools.DefaultWorkFlow;
import org.apache.ofbiz.base.lang.Factory;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.LocalDispatcher;

public interface WorkFlowFactory extends Factory<WorkFlow, String> {
    public void setDelegator(Delegator delegator);
}
