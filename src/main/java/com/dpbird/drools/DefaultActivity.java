package com.dpbird.drools;

import com.dpbird.workflow.Activity;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import java.util.List;

public class DefaultActivity implements Activity {
    private Delegator delegator;
    private String activityName;

    public DefaultActivity(Delegator delegator, String activityName) {
        this.delegator = delegator;
        this.activityName = activityName;
        String workEffortId = delegator.getNextSeqId("WorkEffort");
        GenericValue workEffort = delegator.makeValue("WorkEffort",
                UtilMisc.toMap("workEffortId", workEffortId,
                        "workEffortTypeId", "ACTIVITY",
                        "currentStatusId", "WEPR_IN_PROGRESS",
                        "workEffortName", activityName));
        // add more properties
        try {
            workEffort.create();
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
        return null;
    }

    @Override
    public String getStatusId() {
        return null;
    }

    @Override
    public List<String> getAssignedPartyIds() {
        return null;
    }

    @Override
    public List<String> getAssignedRoleTypeIds() {
        return null;
    }
}
