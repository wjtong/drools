package com.dpbird.drools;

import com.dpbird.workflow.AbstractActivity;
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

public class DefaultActivity extends AbstractActivity {

    public DefaultActivity(Delegator delegator, String workFlowId, String activityName) {
        super(delegator, workFlowId, activityName);
        if (activityName.equals(WorkFlow.NAME_NA)) {
            this.statusId = WorkFlow.STATUS_NA;
        } else {
            createActivityWorkEffort(workFlowId);
        }
    }

    public DefaultActivity(Delegator delegator, String activityId) {
        super(delegator, activityId);
    }

    public DefaultActivity(Delegator delegator, String activityId, String activityName, String statusId) {
        super(delegator, activityId);
        this.delegator = delegator;
        this.activityId = activityId;
        this.activityName = activityName;
        this.statusId = statusId;
    }

}
