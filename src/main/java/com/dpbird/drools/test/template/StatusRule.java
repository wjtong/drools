package com.dpbird.drools.test.template;

public class StatusRule {
    private String conditionStatus;
    private String resultStatus;

    public StatusRule(String conditionStatus, String resultStatus) {
        this.conditionStatus = conditionStatus;
        this.resultStatus = resultStatus;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }
}
