package com.dpbird.drools;

import com.dpbird.workflow.Activity;
import com.dpbird.workflow.WorkFlow;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import java.util.List;
import java.util.Map;

public class DefaultWorkFlow implements WorkFlow {
    private String workFlowId;
    private Delegator delegator;
    private GenericValue workFlowWorkEffort;
    private Activity activeActivity;
    private String statusId;

    public DefaultWorkFlow(Delegator delegator, String workFlowId) {
        this.delegator = delegator;
        this.workFlowId = workFlowId;
        try {
            this.workFlowWorkEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workFlowId), true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getWorkFlowId() {
        return workFlowId;
    }

    @Override
    public String completeActivity(String activityId, String code, String note, Map<String, Object> infoMap) {
        return null;
    }

    @Override
    public String getActiveName() {
        return this.activeActivity.getActivityName();
    }

    @Override
    public String getStatusId() {
        return this.statusId;
    }

    @Override
    public void setActiveName(String activeName) {
        Activity activity = new DefaultActivity(delegator, activeName);
        this.activeActivity = activity;
    }

    @Override
    public void setStatusId(String statusId) {
        this.statusId = statusId;
        workFlowWorkEffort.put("currentStatusId", statusId);
        try {
            workFlowWorkEffort.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setActiveActivity(Activity activity) {
        this.activeActivity = activity;
    }

    @Override
    public Activity getActiveActivity() {
        if (activeActivity == null) { // 查找数据库WorkEffort
            try {
                delegator.findByAnd("WorkEffort",
                        UtilMisc.toMap("workEffortParentId", workFlowId,
                                "workEffortTypeId", "ACTIVITY",
                                "currentStatusId", "WEPR_IN_PROGRESS"), null, false);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return this.activeActivity;
    }

}
