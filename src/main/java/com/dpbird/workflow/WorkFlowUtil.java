package com.dpbird.workflow;

import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericPK;
import org.apache.ofbiz.entity.GenericValue;

import java.util.*;

public class WorkFlowUtil {
    public static final String module = WorkFlowUtil.class.getName();

    // 传入的参数可能是代表项目的WorkEffort，也可能是CustRequest等其它需要审批的对象
    public static WorkFlow createWorkFlow(GenericValue genericValue) throws GenericEntityException {
        if (genericValue == null) {
            return null;
        }
        Delegator delegator = genericValue.getDelegator();
        String workFlowId = getObjectWorkFlowId(genericValue);
        if (workFlowId == null) {
            workFlowId = delegator.getNextSeqId("WorkEffort");
            GenericValue workEffort = delegator.makeValue("WorkEffort",
                    UtilMisc.toMap("workEffortId", workFlowId,
                            "workEffortTypeId", WorkFlow.WF_WORKFLOW_TYPE,
                            "currentStatusId", WorkFlow.WF_STATUS_PLANNING));
            // add more properties
            workEffort.create();
            GenericValue workEffortAttribute = delegator.makeValue("WorkEffortAttribute",
                    UtilMisc.toMap("workEffortId", workFlowId,
                            "attrName", "workFlowId"));
            workEffortAttribute.create();
        }

        // 保存genericValue和WorkEffort(workflow)之间的关系
        WorkFlowFactory workFlowFactory = getWorkFlowFactory(delegator);
        return workFlowFactory.getInstance(workFlowId);
    }

    public static WorkFlowFactory getWorkFlowFactory(Delegator delegator) {
        WorkFlowFactory workFlowFactory = null;
        Iterator<WorkFlowFactory> iter = ServiceLoader.load(WorkFlowFactory.class).iterator();
        if (iter.hasNext()) {
            workFlowFactory = iter.next();
            if (Debug.verboseOn()) {
                Debug.logVerbose("WorkFlow factory set to " + workFlowFactory.getClass().getName(), module);
            }
        } else {
            Debug.logWarning("WorkFlow factory not found", module);
        }
        workFlowFactory.setDelegator(delegator);
        return workFlowFactory;
    }

    public static void completeActivity(Delegator delegator, String activityId, String code, String note, Map<String, Object> infoMap) {
        String workFlowId = null;
        try {
            GenericValue activityGenericValue = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", activityId), true);
            workFlowId = activityGenericValue.getString("workEffortParentId");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        WorkFlowFactory workFlowFactory = getWorkFlowFactory(delegator);
        WorkFlow workFlow = workFlowFactory.getInstance(workFlowId);
        workFlow.completeActivity(activityId, code, note, infoMap);
    }

    public static String getObjectWorkFlowId(GenericValue genericValue) {
        return getObjectAttribute(genericValue, "workFlowId");
    }

    public static String getObjectAttribute(GenericValue genericValue, String attrName) {
        Delegator delegator = genericValue.getDelegator();
        String attrEntityName = genericValue.getEntityName() + "Attribute";
        GenericPK genericPK = genericValue.getPrimaryKey();
        Map<String, Object> fieldCondition = new HashMap<>();
        fieldCondition.putAll(genericPK);
        fieldCondition.put("attrName", attrName);
        try {
            GenericValue attribute = delegator.findOne(attrEntityName, fieldCondition, false);
            if (attribute == null) {
                return null;
            } else {
                return attribute.getString("attrValue");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
