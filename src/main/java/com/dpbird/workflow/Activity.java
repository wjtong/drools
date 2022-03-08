package com.dpbird.workflow;

import java.util.List;
import java.util.Map;

public interface Activity {
    /**
     * 完成当前节点
     *
     * @param code 代表APPROVE, DISAPPROVE等
     * @param note 备注
     * @param infoMap 其它参数
     * @return String 下一个节点的workEffortId
     */
    public void completeActivity(String code, String note, Map<String, Object> infoMap);

    public String getActivityName();
    public String getActivityId();
    public String getStatusId();
    public List<String> getAssignedPartyIds();
    public List<String> getAssignedRoleTypeIds();
}