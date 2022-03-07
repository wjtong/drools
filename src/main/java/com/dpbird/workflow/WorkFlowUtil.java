package com.dpbird.workflow;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import java.util.Map;

public class WorkFlowUtil {
    // 传入的参数可能是代表项目的WorkEffort，也可能是CustRequest等其它需要审批的对象
    public static WorkFlow createWorkFlow(GenericValue genericValue) throws GenericEntityException {
        if (genericValue == null) {
            return null;
        }
        Delegator delegator = genericValue.getDelegator();
        WorkFlowFactory workFlowFactory = new WorkFlowFactory(delegator);
        String workEffortId = delegator.getNextSeqId("WorkEffort");
        GenericValue workEffort = delegator.makeValue("WorkEffort",
                UtilMisc.toMap("workEffortId", workEffortId,
                        "workEffortTypeId", "WORK_FLOW",
                        "currentStatusId", "WEPR_PLANNING"));
        // add more properties
        workEffort.create();

        // 保存genericValue和WorkEffort(workflow)之间的关系
        return workFlowFactory.getInstance(workEffortId);
    }

    public static String completeActivity(Delegator delegator, String activityId, String code, String note, Map<String, Object> infoMap) {
        WorkFlowFactory workFlowFactory = new WorkFlowFactory(delegator);
        WorkFlow workFlow = workFlowFactory.getInstance(activityId);
        return workFlow.completeActivity(activityId, code, note, infoMap);
    }
}
