package com.dpbird.workflow;

import com.dpbird.drools.DefaultActivity;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractWorkFlow implements WorkFlow {
    protected String workFlowId;
    protected Delegator delegator;
    protected GenericValue workFlowWorkEffort;
    protected List<Activity> activeActivities = new ArrayList<>();
    protected String statusId;

    public AbstractWorkFlow(Delegator delegator, String workFlowId) {
        this.delegator = delegator;
        this.workFlowId = workFlowId;
        try {
            this.workFlowWorkEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workFlowId), false);
            statusId = workFlowWorkEffort.getString("currentStatusId");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (statusId.equals(WorkFlow.WF_STATUS_PLANNING)) {
            fireRules();
        }
    }

    @Override
    public String getWorkFlowId() {
        return workFlowId;
    }

    @Override
    public void completeActivity(String activityId, String code, String note, Map<String, Object> infoMap) {
        Activity activity = new DefaultActivity(delegator, activityId);
        activity.completeActivity(code, note, infoMap);
        fireRules();
    }

    @Override
    public List<String> getActiveNames() {
        List<String> activeNames = new ArrayList<>();
        for (Activity activity: activeActivities) {
            activeNames.add(activity.getActivityName());
        }
        return activeNames;
    }

    @Override
    public String getStatusId() {
        return this.statusId;
    }

    @Override
    public void setActiveName(String activeName) {
        Activity activity = new DefaultActivity(delegator, workFlowId, activeName);
//        this.activeActivity = activity;
        addActiveActivity(activity);
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
        addActiveActivity(activity);
    }

//    @Override
//    public Activity getActiveActivity() {
//        if (activeActivity == null) { // 查找数据库WorkEffort
//            try {
//                List<GenericValue> activityGenericValues = delegator.findByAnd("WorkEffort",
//                        UtilMisc.toMap("workEffortParentId", workFlowId,
//                                "workEffortTypeId", "ACTIVITY",
//                                "currentStatusId", "WEPR_IN_PROGRESS"),
//                        null, false);
//                if (UtilValidate.isEmpty(activityGenericValues)) {
//                    return null;
//                }
//                for (GenericValue activityGenericValue:activityGenericValues) {
//                    Activity activity = new DefaultActivity(delegator, activityGenericValue.getString("workEffortId"));
//                    addActiveActivity(activity);
//                }
//            } catch (GenericEntityException e) {
//                e.printStackTrace();
//            }
//            activeActivity = activeActivities.get(0);
//        }
//        return this.activeActivity;
//    }

    @Override
    public List<Activity> getActiveActivities() {
        return this.activeActivities;
    }

    protected abstract void fireRules();
    protected abstract void assignPartiesToActivity(Activity activity);

    protected void assignPartiesToActivity(Activity activity, List<String> partyIds, String roleTypeId, String statusId) {

    }

    private void addActiveActivity(Activity activity) {
        this.activeActivities.add(activity);
    }

}
