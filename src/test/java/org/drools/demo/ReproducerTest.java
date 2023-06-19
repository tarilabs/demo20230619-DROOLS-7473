
package org.drools.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static utils.shim.Map.of;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReproducerTest {
    static final Logger LOG = LoggerFactory.getLogger(ReproducerTest.class);

    private DMNRuntime dmnRuntime;
    private DMNModel dmnModelUT;
    
    @Before
    public void init() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        final String namespace = "https://kiegroup.org/dmn/_5DFCB15D-817A-4B11-ADD3-2504CB6977CD";
        final String modelName = "reproducer";
        dmnModelUT = dmnRuntime.getModel(namespace, modelName);
    }
    
    @Test
    public void test() {
        Map<String, ?> borrowerTaxInfo = of("dateOfRegistration", "2023-06-19");
        
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("my input", borrowerTaxInfo);

        LOG.info("Evaluating DMN model");
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModelUT, dmnContext);

        LOG.info("Checking results: {}", dmnResult);
        assertFalse(dmnResult.hasErrors());

        assertEquals(DecisionEvaluationStatus.SUCCEEDED, dmnResult.getDecisionResultByName("apply").getEvaluationStatus());
        assertEquals(Boolean.TRUE, dmnResult.getDecisionResultByName("apply").getResult());
    }
}