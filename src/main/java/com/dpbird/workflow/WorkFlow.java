package com.dpbird.workflow;

import java.util.List;
import java.util.Map;

public interface WorkFlow {
    public static String WF_STATUS_PLANNING = "WEPR_PLANNING";
    public static String WF_STATUS_IN_PROGRESS = "WEPR_IN_PROGRESS";
    public static String WF_STATUS_COMPLETE = "WEPR_COMPLETE";
    public static String PA_ROLE_TYPE = "WF_OWNER";
    public static String PA_STATUS = "PRTYASGN_ASSIGNED";

    public String getWorkFlowId();
    /**
     * 完成当前节点
     *
     * @param activityId 节点对应的workEffortId
     * @param code 代表APPROVE, DISAPPROVE等
     * @param note 备注
     * @param infoMap 其它参数
     * @return String 下一个节点的workEffortId
     */
    public void completeActivity(String activityId, String code, String note, Map<String, Object> infoMap);

    public String getStatusId();
    public List<String> getActiveNames();
    public void setActiveName(String activityName);
    public void setStatusId(String statusId);
    public void setActiveActivity(Activity activity);
    public List<Activity> getActiveActivities();
    public void assignPartiesToActivity(Activity activity, List<String> partyLabels);
}
