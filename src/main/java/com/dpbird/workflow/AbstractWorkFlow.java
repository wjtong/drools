package com.dpbird.workflow;

import com.dpbird.drools.DefaultActivity;
import com.dpbird.drools.DefaultWorkFlow;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractWorkFlow implements WorkFlow {
    private final static String module = AbstractWorkFlow.class.getName();

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
            // find active activities from delegator
            loadActiveActivities(delegator, workFlowId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (statusId.equals(WorkFlow.WF_STATUS_PLANNING)) {
            fireRules();
        }
    }

    private void loadActiveActivities(Delegator delegator, String workFlowId) {
        try {
//            List<GenericValue> activitieGvs = delegator.findByAnd("WorkEffort",
//                    UtilMisc.toMap("workEffortParentId", workFlowId,
//                            "workEffortTypeId", "ACTIVITY",
//                            "currentStatusId", WorkFlow.WF_STATUS_IN_PROGRESS),
//                    null,
//                    false);
            List<GenericValue> activitieGvs = delegator.findByAnd("WorkEffort",
                    UtilMisc.toMap("workEffortParentId", workFlowId,
                            "workEffortTypeId", "ACTIVITY"),
                    null,
                    false);
            for (GenericValue activityGv:activitieGvs) {
                addActiveActivity(activityFromGv(activityGv));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    protected abstract Activity activityFromGv(GenericValue activityGv);

    @Override
    public String getWorkFlowId() {
        return workFlowId;
    }

    @Override
    public GenericValue completeActivity(String activityId, String code, String note, Map<String, Object> infoMap) {
//        Activity activity = new DefaultActivity(delegator, activityId);
        GenericValue activityGv = null;
        for (Activity activity:activeActivities) {
            if (activity.getActivityId().equals(activityId)) {
                activityGv = activity.completeActivity(code, note, infoMap);
                break;
            }
        }
        fireRules();
        return activityGv;
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
        this.activeActivities = new ArrayList<>();
        Activity activity = new DefaultActivity(delegator, workFlowId, activeName, WorkFlow.NAME_NA);
//        this.activeActivity = activity;
        addActiveActivity(activity);
    }

    @Override
    public void setActiveName(String activeName, String assignee, String waitLock) {
        if (UtilValidate.isEmpty(this.activeActivities)) {
            this.activeActivities = new ArrayList<>();
        }
        Activity activity = new DefaultActivity(delegator, workFlowId, activeName, waitLock);
        if (UtilValidate.isNotEmpty(waitLock)) {
            activity.setWaitLock(waitLock);
        }
//        this.activeActivity = activity;
        addActiveActivity(activity);
        assignPartiesToActivity(activity, UtilMisc.toList(assignee));
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
//    protected abstract void assignPartiesToActivity(Activity activity);

    protected void assignPartiesToActivityWithRole(Activity activity, List<String> partyIds, String roleTypeId, String statusId)
            throws GenericEntityException {
        for (String partyId:partyIds) {
            // 先检查这个Party是否有这个Role，如果没有就添加一个
            Map<String, Object> partyRoleKeyMap = UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId);
            GenericValue partyRole = delegator.findOne("PartyRole", partyRoleKeyMap, true);
            if (UtilValidate.isEmpty(partyRole)) {
                partyRole = delegator.makeValidValue("PartyRole", partyRoleKeyMap);
                partyRole.create();
            }
            GenericValue workEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment",
                    UtilMisc.toMap("workEffortId", activity.getActivityId(),
                            "partyId", partyId,
                            "roleTypeId", roleTypeId,
                            "statusId", statusId,
                            "fromDate", UtilDateTime.nowTimestamp()));
            // add more properties
            workEffortPartyAssignment.create();
        }

    }

    private void addActiveActivity(Activity activity) {
        this.activeActivities.add(activity);
    }

    @Override
    public boolean noActive(String activityName) {
        Debug.logInfo("------------------------------------ noActive " + activityName, module);
        if (UtilValidate.isEmpty(this.activeActivities)) {
            return true;
        }
        for (Activity activity:this.activeActivities) {
            if (activity.getActivityName().equals(activityName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkWaitLock(String activityName) {

        String waitLock = WorkFlow.NAME_NA;
        if (UtilValidate.isEmpty(this.activeActivities)) {
            return true;
        }
        for (Activity activity:this.activeActivities) {
            if (activity.getActivityName().equals(activityName)) {
                waitLock = activity.getWaitLock();
            }
        }
        if (waitLock.equals(WorkFlow.NAME_NA)) {
            return true;
        }
        for (Activity activity:this.activeActivities) {
            if (waitLock.equals(activity.getWaitLock())) {
                if (!activity.getStatusId().equals(WorkFlow.WF_STATUS_COMPLETE)) {
                    return false;
                }
            }
        }
        return true;
    }
}
