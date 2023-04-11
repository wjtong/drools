package com.dpbird.drools;

public class DefaultWorkFlowRule {
    private String workFlowStatusId;
    private String activityName;
    private String activityStatusId;
    private String nextActivityName;
    private String nextWorkFlowStatusId;
    private String nextActivityAssignee;

    public DefaultWorkFlowRule(String workFlowStatusId, String activityName, String activityStatusId,
                               String nextActivityName, String nextWorkFlowStatusId, String nextActivityAssignee) {
        this.workFlowStatusId = workFlowStatusId;
        this.activityName = activityName;
        this.activityStatusId = activityStatusId;
        this.nextActivityName = nextActivityName;
        this.nextWorkFlowStatusId = nextWorkFlowStatusId;
        this.nextActivityAssignee = nextActivityAssignee;
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
}
