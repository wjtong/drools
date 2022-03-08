package com.dpbird.drools.test;

import com.dpbird.workflow.WorkFlowUtil;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.order.shoppingcart.CartItemModifyException;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestEvents {
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

        execute( ks, kc );
        return null;
    }

    private static void execute( KieServices ks, KieContainer kc ) {
        // From the container, a session is created based on
        // its definition and configuration in the META-INF/kmodule.xml file
        KieSession ksession = kc.newKieSession("HelloWorldKS");

        // Once the session is created, the application can interact with it
        // In this case it is setting a global as defined in the
        // org/drools/examples/helloworld/HelloWorld.drl file
        ksession.setGlobal( "list", new ArrayList<Object>() );

        // The application can also setup listeners
        ksession.addEventListener( new DebugAgendaEventListener() );
        ksession.addEventListener( new DebugRuleRuntimeEventListener() );

        // Set up a file based audit logger
        KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession, "./helloworld" );

        // To set up a ThreadedFileLogger, so that the audit view reflects events whilst debugging,
        // uncomment the next line
        // KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( ksession, "./helloworld", 1000 );

        // The application can insert facts into the session
        final Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        ksession.insert( message );

        // and fire the rules
        ksession.fireAllRules();

        // Close logger
        logger.close();

        // and then dispose the session
        ksession.dispose();
    }

    public static class Message {
        public static final int HELLO   = 0;
        public static final int GOODBYE = 1;

        private String          message;

        private int             status;

        public Message() {

        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(final int status) {
            this.status = status;
        }

        public static Message doSomething(Message message) {
            return message;
        }

        public boolean isSomething(String msg,
                                   List<Object> list) {
            list.add( this );
            return this.message.equals( msg );
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

}
