package com.dpbird.drools.test;

import com.dpbird.drools.test.template.StatusRule;
import com.dpbird.drools.test.template.TestDataProvider;
import com.dpbird.workflow.WorkFlowUtil;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.order.shoppingcart.CartItemModifyException;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.drools.template.DataProviderCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestEvents {
    private static final String module = TestEvents.class.getName();
    public static Object helloWorld(HttpServletRequest request, Map<String, Object> actionParameters)
            throws GenericEntityException, GenericServiceException, CartItemModifyException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        String otherParm = (String) actionParameters.get("otherParm");
        String partyId = (String) actionParameters.get("partyId");
        GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));

        KieServices ks = KieServices.get();
        KieContainer kc = ks.getKieClasspathContainer();

        execute(ks, kc);
        return null;
    }

    private static void execute(KieServices ks, KieContainer kc) {
        // From the container, a session is created based on
        // its definition and configuration in the META-INF/kmodule.xml file
        KieSession ksession = kc.newKieSession("HelloWorldKS");
        KieBase kieBase = kc.getKieBase();

        // Once the session is created, the application can interact with it
        // In this case it is setting a global as defined in the
        // org/drools/examples/helloworld/HelloWorld.drl file
        ksession.setGlobal("list", new ArrayList<Object>());

        // The application can also setup listeners
        ksession.addEventListener(new DebugAgendaEventListener());
        ksession.addEventListener(new DebugRuleRuntimeEventListener());

        // Set up a file based audit logger
        KieRuntimeLogger logger = ks.getLoggers().newFileLogger(ksession, "./helloworld");

        // To set up a ThreadedFileLogger, so that the audit view reflects events whilst debugging,
        // uncomment the next line
        // KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( ksession, "./helloworld", 1000 );

        // The application can insert facts into the session
        final Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        ksession.insert(message);

        // and fire the rules
        ksession.fireAllRules();

        // Close logger
        logger.close();

        // and then dispose the session
        ksession.dispose();
    }

    public static class Message {
        public static final String HELLO = "0";
        public static final String GOODBYE = "1";
        public static final String NICE = "2";
        public static final String GOOD = "3";
        public static final String PERFECT = "4";
        public static final String GREAT = "5";
        public static final String FANTASTIC = "6";
        public static final String FINALLY = "7";
        public static final String END = "8";
        public static final String FINISHED = "9";

        private String message;

        private String status;

        public Message() {

        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public static Message doSomething(Message message) {
            return message;
        }

        public boolean isSomething(String msg,
                                   List<Object> list) {
            list.add(this);
            return this.message.equals(msg);
        }
    }

    public static Object createWorkFlow(HttpServletRequest request, Map<String, Object> actionParameters)
            throws GenericEntityException, GenericServiceException, CartItemModifyException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        String custRequestId = (String) actionParameters.get("custRequestId");
        GenericValue custRequest = delegator.findOne("CustRequest", false, UtilMisc.toMap("custRequestId", custRequestId));

        WorkFlowUtil.createWorkFlow(custRequest);
        return null;
    }

    public static Object createWorkFlowTemplate(HttpServletRequest request, Map<String, Object> actionParameters)
            throws GenericEntityException, GenericServiceException, CartItemModifyException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        String custRequestId = (String) actionParameters.get("custRequestId");
        GenericValue custRequest = delegator.findOne("CustRequest", false, UtilMisc.toMap("custRequestId", custRequestId));

        WorkFlowUtil.createWorkFlow(custRequest);
        return null;
    }

    public static Object completeActivity(HttpServletRequest request, Map<String, Object> actionParameters)
            throws GenericEntityException, GenericServiceException, CartItemModifyException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        String activityId = (String) actionParameters.get("activityId");
        String code = (String) actionParameters.get("code");
        String note = (String) actionParameters.get("note");

        WorkFlowUtil.completeActivity(delegator, activityId, code, note, null);
        return null;
    }

    public static Object helloWorldDynamic(HttpServletRequest request, Map<String, Object> actionParameters)
            throws GenericEntityException, GenericServiceException, CartItemModifyException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        String otherParm = (String) actionParameters.get("otherParm");
        String partyId = (String) actionParameters.get("partyId");
        GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));

        /***************** rules *****************************/
        StringBuffer sb = new StringBuffer();
        sb.append("package com.dpbird.drools.test.dynamic;\n");
        sb.append("import com.dpbird.drools.test.TestEvents.Message;\n");
        sb.append("global java.util.List list\n");
        sb.append("rule \"Hello World\"\n");
        sb.append("dialect \"mvel\"\n");
        sb.append("when\n");
        sb.append("m : Message( status == Message.HELLO, message : message )\n");
        sb.append("then\n");
        sb.append("System.out.println( message );\n");
        sb.append("modify ( m ) { message = \"Goodbye cruel world\",status = Message.GOODBYE };\n");
        sb.append("end\n");

        sb.append("rule \"Good Bye\"\n");
        sb.append("dialect \"java\"\n");
        sb.append("when\n");
        sb.append("Message( status == Message.GOODBYE, message : message )\n");
        sb.append("then\n");
        sb.append("System.out.println( message );\n");
        sb.append("end\n");
        String rules = sb.toString();
        /***************** end rules *************************/

        KieServices ks = KieServices.get();
        KieContainer kc = ks.getKieClasspathContainer();

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(rules, ResourceType.DRL);
        KieSession kieSession = kieHelper.build().newKieSession();
        kieSession.setGlobal("list", new ArrayList<Object>());
        final Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        kieSession.insert(message);
        kieSession.fireAllRules();
        kieSession.dispose();

//        DecisionTableConfiguration decisionTableConfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
//        decisionTableConfiguration.setInputType(DecisionTableInputType.CSV);
//        Resource resource = ResourceFactory.newClassPathResource("example.csv", TestEvents.getClass());
        return null;
    }

    public static Object helloWorldTemplate(HttpServletRequest request, Map<String, Object> actionParameters)
            throws GenericEntityException, GenericServiceException, CartItemModifyException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        String otherParm = (String) actionParameters.get("otherParm");
        String partyId = (String) actionParameters.get("partyId");
        GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));

        String rules = compileRules();

        KieServices ks = KieServices.get();
        KieContainer kc = ks.getKieClasspathContainer();

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(rules, ResourceType.DRL);
        KieSession kieSession = kieHelper.build().newKieSession();
        kieSession.setGlobal("list", new ArrayList<Object>());
        final Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        kieSession.insert(message);
        kieSession.fireAllRules();
        kieSession.dispose();

//        DecisionTableConfiguration decisionTableConfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
//        decisionTableConfiguration.setInputType(DecisionTableInputType.CSV);
//        Resource resource = ResourceFactory.newClassPathResource("example.csv", TestEvents.getClass());
        return null;
    }

    private static String compileRules() {
        String result = null;
        List<StatusRule> rules = new ArrayList<>();
        rules.add(new StatusRule(Message.HELLO, Message.FINALLY));
        rules.add(new StatusRule(Message.GOODBYE, Message.END));
        rules.add(new StatusRule(Message.NICE, Message.FINISHED));
        TestDataProvider testDataProvider = new TestDataProvider(rules);
        final DataProviderCompiler converter = new DataProviderCompiler();
        InputStream inputStream = TestDataProvider.class.getResourceAsStream( "HelloWorld.drt" );
        result = converter.compile( testDataProvider, inputStream);
        Debug.logInfo(result, module);
        return result;
    }

}
