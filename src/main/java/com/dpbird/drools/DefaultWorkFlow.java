package com.dpbird.drools;

import com.dpbird.workflow.AbstractWorkFlow;
import com.dpbird.workflow.Activity;
import com.dpbird.workflow.WorkFlow;
import com.dpbird.workflow.WorkFlowUtil;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.List;

public class DefaultWorkFlow extends AbstractWorkFlow {

    public DefaultWorkFlow(Delegator delegator, String workFlowId) {
        super(delegator, workFlowId);
    }

    @Override
    protected void fireRules() {
        KieServices ks = KieServices.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("WorkFlowKS");
        // The application can also setup listeners
        ksession.addEventListener( new DebugAgendaEventListener() );
        ksession.addEventListener( new DebugRuleRuntimeEventListener() );

        // Set up a file based audit logger
        KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession, "./workflow" );

        ksession.insert(this);
        if (UtilValidate.isNotEmpty(activeActivities)) {
            for (Activity activity:activeActivities) {
                ksession.insert(activity);
            }
        }
        // and fire the rules
        ksession.fireAllRules();
        // Close logger
        logger.close();
        // and then dispose the session
        ksession.dispose();
    }

    @Override
    protected void assignPartiesToActivity(Activity activity) {
        String partyId = WorkFlowUtil.getObjectAttribute(this.workFlowWorkEffort, activity.getActivityName());
        if (partyId != null) {
            assignPartiesToActivity(activity, UtilMisc.toList(partyId));
        }
    }

    @Override
    public void assignPartiesToActivity(Activity activity, List<String> partyLabels) {
        List<String> partyIds = partyLabels;
        assignPartiesToActivity(activity, partyIds, WorkFlow.PA_ROLE_TYPE, WorkFlow.PA_STATUS);
    }
}
