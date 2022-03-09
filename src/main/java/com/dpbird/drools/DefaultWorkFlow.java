package com.dpbird.drools;

import com.dpbird.workflow.AbstractWorkFlow;
import com.dpbird.workflow.Activity;
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
    public void assignPartiesToActivity(Activity activity, List<String> partyLabels) {

    }
}
