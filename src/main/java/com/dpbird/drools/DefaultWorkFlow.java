package com.dpbird.drools;

import com.dpbird.drools.test.TestEvents;
import com.dpbird.drools.test.template.StatusRule;
import com.dpbird.drools.test.template.TestDataProvider;
import com.dpbird.workflow.AbstractWorkFlow;
import com.dpbird.workflow.Activity;
import com.dpbird.workflow.WorkFlow;
import com.dpbird.workflow.WorkFlowUtil;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.drools.template.DataProviderCompiler;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DefaultWorkFlow extends AbstractWorkFlow {

    private final static String module = DefaultWorkFlow.class.getName();

    public DefaultWorkFlow(Delegator delegator, String workFlowId) {
        super(delegator, workFlowId);
    }

    @Override
    protected Activity activityFromGv(GenericValue activityGv) {
        return new DefaultActivity(delegator, activityGv.getString("workEffortId"),
                activityGv.getString("workEffortName"),
                activityGv.getString("currentStatusId"),
                activityGv.getString("showAsEnumId"));
    }

    @Override
    protected void fireRules() {
        if (statusId.equals(WorkFlow.WF_STATUS_PLANNING)) {
            addNaActivity();
        }
        KieServices ks = KieServices.get();
        KieContainer kc = ks.getKieClasspathContainer();


        KieHelper kieHelper = new KieHelper();
        String rules = compileRules();
        kieHelper.addContent(rules, ResourceType.DRL);
        KieSession kieSession = kieHelper.build().newKieSession();
        kieSession.insert(this);
        if (UtilValidate.isNotEmpty(activeActivities)) {
            for (Activity activity:activeActivities) {
                kieSession.insert(activity);
            }
        }
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    private void addNaActivity() {
        setActiveName(WorkFlow.NAME_NA);
    }
//    protected void fireRules() { // no template
//        KieServices ks = KieServices.get();
//        KieContainer kc = ks.getKieClasspathContainer();
//        KieSession ksession = kc.newKieSession("WorkFlowKS");
//        // The application can also setup listeners
//        ksession.addEventListener( new DebugAgendaEventListener() );
//        ksession.addEventListener( new DebugRuleRuntimeEventListener() );
//
//        // Set up a file based audit logger
//        KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession, "./workflow" );
//
//        ksession.insert(this);
//        if (UtilValidate.isNotEmpty(activeActivities)) {
//            for (Activity activity:activeActivities) {
//                ksession.insert(activity);
//            }
//        }
//        // and fire the rules
//        ksession.fireAllRules();
//        // Close logger
//        logger.close();
//        // and then dispose the session
//        ksession.dispose();
//    }

//    @Override
//    protected void assignPartiesToActivity(Activity activity) {
//        String partyId = WorkFlowUtil.getObjectAttribute(this.workFlowWorkEffort, activity.getActivityName());
//        if (partyId != null) {
//            assignPartiesToActivity(activity, UtilMisc.toList(partyId));
//        }
//    }

    @Override
    public void assignPartiesToActivity(Activity activity, List<String> partyLabels) {
        List<String> partyIds = partyLabels;
        try {
            assignPartiesToActivityWithRole(activity, partyIds, WorkFlow.PA_ROLE_TYPE, WorkFlow.PA_STATUS);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            // TODO: will be handled later
        }
    }

    private String compileRules() {
        String templateId = "DefaultWorkFlow";
        String result = null;
        List<DefaultWorkFlowRule> rules = new ArrayList<>();
        List<GenericValue> ruleParams;
        try {
            ruleParams = delegator.findByAnd("DefaultWorkFlowRule",
                    UtilMisc.toMap("templateId", templateId, "isActive", "Y"),
                    null, true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
        for (GenericValue ruleParam:ruleParams) {
            rules.add(new DefaultWorkFlowRule(ruleParam.getString("workFlowStatusId"),
                    ruleParam.getString("activityName"),
                    ruleParam.getString("activityStatusId"),
                    ruleParam.getString("nextActivityName"),
                    ruleParam.getString("nextWorkFlowStatusId"),
                    ruleParam.getString("nextActivityAssignee"),
                    ruleParam.getString("waitLock")));
        }
        DefaultDataProvider dataProvider = new DefaultDataProvider(rules);
        final DataProviderCompiler converter = new DataProviderCompiler();
        InputStream inputStream = DefaultDataProvider.class.getResourceAsStream( templateId + ".drt" );
        result = converter.compile( dataProvider, inputStream);
        Debug.logInfo(result, module);
        return result;
    }
}
