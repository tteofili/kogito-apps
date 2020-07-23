/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.explainability.integrationtests.dmn;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.Saliency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanEligibilityDmnLimeExplainerTest {

    @Test
    void testLoanEligibilityDMNExplanation() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/LoanEligibility.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String FRAUD_NS = "https://github.com/kiegroup/kogito-examples/dmn-quarkus-listener-example";
        final String FRAUD_NAME = "LoanEligibility";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, FRAUD_NS, FRAUD_NAME);

        final Map<String, Object> client = new HashMap<>();
        client.put("age", 43);
        client.put("salary", 1950);
        client.put("existing payments", 100);
        final Map<String, Object> loan = new HashMap<>();
        loan.put("duration", 15);
        loan.put("installment", 100);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("client", client);
        contextVariables.put("loan", loan);

        PredictionProvider model = new DecisionModelWrapper(decisionModel);
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        PredictionInput predictionInput = new PredictionInput(features);
        List<PredictionOutput> predictionOutputs = model.predict(List.of(predictionInput));
        Prediction prediction = new Prediction(predictionInput, predictionOutputs.get(0));
        LimeExplainer limeExplainer = new LimeExplainer(100, 1);
        Saliency saliency = limeExplainer.explain(prediction, model);

        assertNotNull(saliency);
        List<String> strings = saliency.getTopFeatures(2).stream().map(f -> f.getFeature().getName()).collect(Collectors.toList());
        assertTrue(strings.contains("installment") || strings.contains("duration"));
    }
}