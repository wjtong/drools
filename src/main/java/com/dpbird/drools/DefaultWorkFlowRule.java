package com.dpbird.drools;

import com.dpbird.workflow.WorkFlow;
import org.apache.ofbiz.base.util.UtilValidate;

public class DefaultWorkFlowRule {
    private String workFlowStatusId;
    private String activityName;
    private String activityStatusId;
    private String nextActivityName;
    private String nextWorkFlowStatusId;
    private String nextActivityAssignee;
    private String waitLock;

    public DefaultWorkFlowRule(String workFlowStatusId, String activityName, String activityStatusId,
                               String nextActivityName, String nextWorkFlowStatusId,
                               String nextActivityAssignee, String waitLock) {
        this.workFlowStatusId = workFlowStatusId;
        this.activityName = activityName;
        this.activityStatusId = activityStatusId;
        this.nextActivityName = nextActivityName;
        this.nextWorkFlowStatusId = nextWorkFlowStatusId;
        this.nextActivityAssignee = nextActivityAssignee;
        this.waitLock = waitLock;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getWorkFlowStatusId() {
        return workFlowStatusId;
    }

    public void setWorkFlowStatusId(String workFlowStatusId) {
        this.workFlowStatusId = workFlowStatusId;
    }

    public String getNextWorkFlowStatusId() {
        return nextWorkFlowStatusId;
    }

    public void setNextWorkFlowStatusId(String nextWorkFlowStatusId) {
        this.nextWorkFlowStatusId = nextWorkFlowStatusId;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityStatusId() {
        return activityStatusId;
    }

    public void setActivityStatusId(String activityStatusId) {
        this.activityStatusId = activityStatusId;
    }

    public String getNextActivityName() {
        return nextActivityName;
    }

    public void setNextActivityName(String nextActivityName) {
        this.nextActivityName = nextActivityName;
    }

    public String getNextActivityAssignee() {
        return nextActivityAssignee;
    }

    public void setNextActivityAssignee(String nextActivityAssignee) {
        this.nextActivityAssignee = nextActivityAssignee;
    }

    public String getWaitLock() {
        if (UtilValidate.isEmpty(this.waitLock)) {
            return WorkFlow.NAME_NA;
        }
        return waitLock;
    }

    public void setWaitLock(String waitLock) {
        this.waitLock = waitLock;
    }
}
