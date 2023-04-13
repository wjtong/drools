package com.dpbird.workflow;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractActivity implements Activity {
    protected String activityId;
    protected Delegator delegator;
    protected String activityName;
    protected String statusId;
    protected String waitLock = WorkFlow.NAME_NA;
    protected List<String> assignedPartyIds = new ArrayList<>();
    protected List<String> assignedRoleTypeIds = new ArrayList<>();
    protected GenericValue activityGenericValue = null;

    public AbstractActivity(Delegator delegator, String workFlowId, String activityName, String waitLock) {
        this.delegator = delegator;
        this.activityName = activityName;
        this.waitLock = waitLock;
    }

    protected void createActivityWorkEffort(String workFlowId) {
        try {
            List<GenericValue> activityGenericValues = delegator.findByAnd("WorkEffort",
                    UtilMisc.toMap("workEffortParentId", workFlowId,
                            "workEffortName", activityName,
                            "workEffortTypeId", "ACTIVITY"),
                    null, false);
            if (UtilValidate.isEmpty(activityGenericValues)) {
                String workEffortId = delegator.getNextSeqId("WorkEffort");
                String currentStatusId = WorkFlow.WF_STATUS_IN_PROGRESS;
                GenericValue workEffort = delegator.makeValue("WorkEffort",
                        UtilMisc.toMap("workEffortId", workEffortId,
                                "workEffortTypeId", WorkFlow.WF_ACTIVITY_TYPE,
                                "currentStatusId", currentStatusId,
                                "workEffortParentId", workFlowId,
                                "workEffortName", activityName,
                                "showAsEnumId", waitLock));
                // add more properties
                workEffort.create();
                this.activityId = workEffortId;
                this.statusId = currentStatusId;
                this.activityGenericValue = workEffort;
            } else {
                this.activityGenericValue = EntityUtil.getFirst(activityGenericValues);
                this.activityId = activityGenericValue.getString("workEffortId");
            }
            List<GenericValue> workEffortPartyAssignments = activityGenericValue.getRelated("WorkEffortPartyAssignment",
                    UtilMisc.toMap("statusId", WorkFlow.PA_STATUS), null, true);
            if (UtilValidate.isNotEmpty(workEffortPartyAssignments)) {
                for (GenericValue workEffortPartyAssignment:workEffortPartyAssignments) {
                    this.assignedPartyIds.add(workEffortPartyAssignment.getString("partyId"));
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public AbstractActivity(Delegator delegator, String activityId) {
        this.delegator = delegator;
        try {
            GenericValue activityWorkEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", activityId), false);
            this.activityName = activityWorkEffort.getString("workEffortName");
            this.statusId = activityWorkEffort.getString("currentStatusId");
            this.activityGenericValue = activityWorkEffort;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GenericValue completeActivity(String code, String note, Map<String, Object> infoMap) {
        try {
            activityGenericValue.put("currentStatusId", code);
            activityGenericValue.put("description", note);
            // add note
            activityGenericValue.store();
            statusId = code;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return activityGenericValue;
    }

    @Override
    public String getActivityName() {
        return activityName;
    }

    @Override
    public String getActivityId() {
        return activityId;
    }

    @Override
    public String getStatusId() {
        return this.statusId;
    }

    @Override
    public List<String> getAssignedPartyIds() {
        return this.assignedPartyIds;
    }

    @Override
    public List<String> getAssignedRoleTypeIds() {
        return this.assignedRoleTypeIds;
    }

    @Override
    public void assignParties(List<String> partyIds) {

    }

    @Override
    public void setWaitLock(String waitLock) {
        this.waitLock = waitLock;
    }

    @Override
    public String getWaitLock() {
        return this.waitLock;
    }
}
