package com.dpbird.workflow;

import java.util.List;
import java.util.Map;

public interface WorkFlow {
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
    public String getActiveName();
    public void setActiveName(String activityName);
    public void setStatusId(String statusId);
    public void setActiveActivity(Activity activity);
    public Activity getActiveActivity();
}
