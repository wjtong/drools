package com.dpbird.workflow;

import org.apache.ofbiz.entity.GenericValue;

import java.util.List;
import java.util.Map;

public interface WorkFlow {
    public static String WF_STATUS_PLANNING = "WEPR_PLANNING";
    public static String WF_STATUS_IN_PROGRESS = "WEPR_IN_PROGRESS";
    public static String WF_STATUS_COMPLETE = "WEPR_COMPLETE";
    public static String WF_WORKFLOW_TYPE = "WORK_FLOW";
    public static String WF_ACTIVITY_TYPE = "ACTIVITY";
    public static String PA_ROLE_TYPE = "WF_OWNER";
    public static String PA_STATUS = "PRTYASGN_ASSIGNED";
    public static String NAME_NA = "NA";
    public static String STATUS_NA = "NA";

    public String getWorkFlowId();
    /**
     * 完成当前节点
     *
     * @param activityId 节点对应的workEffortId
     * @param code 完成后WorkEffort的状态，用以代表通过，拒绝等
     * @param note 备注
     * @param infoMap 其它参数
     * @return String 下一个节点的workEffortId
     */
    public GenericValue completeActivity(String activityId, String code, String note, Map<String, Object> infoMap);

    public String getStatusId();
    public List<String> getActiveNames();
    public void setActiveName(String activityName);
    public void setActiveName(String activityName, String assignee, String waitLock);
    public void setStatusId(String statusId);
    public void setActiveActivity(Activity activity);
    public List<Activity> getActiveActivities();
    public void assignPartiesToActivity(Activity activity, List<String> partyLabels);
    public boolean noActive(String activityName);
    public boolean checkWaitLock(String activityName);
}
