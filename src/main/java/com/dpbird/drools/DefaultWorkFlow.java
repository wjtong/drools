package com.dpbird.drools;

import com.dpbird.workflow.Activity;
import com.dpbird.workflow.WorkFlow;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.List;
import java.util.Map;

public class DefaultWorkFlow implements WorkFlow {
    private String workFlowId;
    private Delegator delegator;
    private GenericValue workFlowWorkEffort;
    private Activity activeActivity = null;
    private String statusId;

    public DefaultWorkFlow(Delegator delegator, String workFlowId) {
        this.delegator = delegator;
        this.workFlowId = workFlowId;
        try {
            this.workFlowWorkEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workFlowId), false);
            statusId = workFlowWorkEffort.getString("currentStatusId");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (statusId.equals("WEPR_PLANNING")) {
            fireRules();
        }
    }

    private void fireRules() {
        KieServices ks = KieServices.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("WorkFlowKS");
        // The application can also setup listeners
        ksession.addEventListener( new DebugAgendaEventListener() );
        ksession.addEventListener( new DebugRuleRuntimeEventListener() );

        // Set up a file based audit logger
        KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession, "./workflow" );

        ksession.insert(this);
        if (activeActivity != null) {
            ksession.insert(activeActivity);
        }
        // and fire the rules
        ksession.fireAllRules();
        // Close logger
        logger.close();
        // and then dispose the session
        ksession.dispose();
    }

    @Override
    public String getWorkFlowId() {
        return workFlowId;
    }

    @Override
    public void completeActivity(String activityId, String code, String note, Map<String, Object> infoMap) {
        Activity activity = new DefaultActivity(delegator, activityId);
        activity.completeActivity(code, note, infoMap);
        this.activeActivity = activity;
        fireRules();
    }

    @Override
    public String getActiveName() {
        return this.activeActivity.getActivityName();
    }

    @Override
    public String getStatusId() {
        return this.statusId;
    }

    @Override
    public void setActiveName(String activeName) {
        Activity activity = new DefaultActivity(delegator, workFlowId, activeName);
        this.activeActivity = activity;
    }

    @Override
    public void setStatusId(String statusId) {
        this.statusId = statusId;
        workFlowWorkEffort.put("currentStatusId", statusId);
        try {
            workFlowWorkEffort.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setActiveActivity(Activity activity) {
        this.activeActivity = activity;
    }

    @Override
    public Activity getActiveActivity() {
        if (activeActivity == null) { // 查找数据库WorkEffort
            try {
                delegator.findByAnd("WorkEffort",
                        UtilMisc.toMap("workEffortParentId", workFlowId,
                                "workEffortTypeId", "ACTIVITY",
                                "currentStatusId", "WEPR_IN_PROGRESS"), null, false);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return this.activeActivity;
    }

}
