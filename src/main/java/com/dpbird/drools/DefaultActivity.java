package com.dpbird.drools;

import com.dpbird.workflow.Activity;
import com.dpbird.workflow.WorkFlow;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultActivity implements Activity {
    private String activityId;
    private Delegator delegator;
    private String activityName;
    private String statusId;
    private List<String> assignedPartyIds = new ArrayList<>();
    private List<String> assignedRoleTypeIds = new ArrayList<>();
    private GenericValue activityGenericValue = null;

    public DefaultActivity(Delegator delegator, String workFlowId, String activityName) {
        this.delegator = delegator;
        this.activityName = activityName;
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
                                "workEffortTypeId", "ACTIVITY",
                                "currentStatusId", currentStatusId,
                                "workEffortParentId", workFlowId,
                                "workEffortName", activityName));
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

    public DefaultActivity(Delegator delegator, String activityId) {
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
    public void completeActivity(String code, String note, Map<String, Object> infoMap) {
        try {
            activityGenericValue.put("currentStatusId", code);
            // add note
            activityGenericValue.store();
            statusId = code;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

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
}
