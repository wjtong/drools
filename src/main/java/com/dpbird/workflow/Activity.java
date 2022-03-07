package com.dpbird.workflow;

import java.util.List;

public interface Activity {
    public String getActivityName();
    public String getActivityId();
    public String getStatusId();
    public List<String> getAssignedPartyIds();
    public List<String> getAssignedRoleTypeIds();
}