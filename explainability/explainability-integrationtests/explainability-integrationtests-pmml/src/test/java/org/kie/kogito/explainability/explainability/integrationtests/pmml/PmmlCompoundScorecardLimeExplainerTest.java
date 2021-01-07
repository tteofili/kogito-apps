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
package org.kie.kogito.explainability.explainability.integrationtests.pmml;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.*;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.pmml.api.runtime.PMMLRuntime;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryInternal.getPMMLRuntime;
import static org.kie.test.util.filesystem.FileUtils.getFile;

@Disabled
class PmmlCompoundScorecardLimeExplainerTest {

    private static PMMLRuntime compoundScoreCardRuntime;

    @BeforeAll
    static void setUpBefore() {
        compoundScoreCardRuntime = getPMMLRuntime(getFile("CompoundNestedPredicateScorecard.pmml"));
        Config.INSTANCE.setAsyncTimeout(25000);
        Config.INSTANCE.setAsyncTimeUnit(TimeUnit.MILLISECONDS);
    }

    @Test
    void testPMMLCompoundScorecard() throws Exception {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeConfig limeConfig = new LimeConfig()
                    .withSamples(300)
                    .withPerturbationContext(new PerturbationContext(random, 1));
            LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("input1", -50));
            features.add(FeatureFactory.newTextFeature("input2", "classB"));
            PredictionInput input = new PredictionInput(features);

            PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
                List<PredictionOutput> outputs = new LinkedList<>();
                for (PredictionInput input1 : inputs) {
                    List<Feature> features1 = input1.getFeatures();
                    CompoundNestedPredicateScorecardExecutor pmmlModel = new CompoundNestedPredicateScorecardExecutor(
                            features1.get(0).getValue().asNumber(), features1.get(1).getValue().asString());
                    PMML4Result result = pmmlModel.execute(compoundScoreCardRuntime);
                    String score = "" + result.getResultVariables().get(CompoundNestedPredicateScorecardExecutor.TARGET_FIELD);
                    String reason1 = "" + result.getResultVariables().get(CompoundNestedPredicateScorecardExecutor.REASON_CODE1_FIELD);
                    PredictionOutput predictionOutput = new PredictionOutput(List.of(
                            new Output("score", Type.TEXT, new Value<>(score), 1d),
                            new Output("reason1", Type.TEXT, new Value<>(reason1), 1d)
                    ));
                    outputs.add(predictionOutput);
                }
                return outputs;
            });
            List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            assertThat(predictionOutputs).isNotNull();
            assertThat(predictionOutputs).isNotEmpty();
            PredictionOutput output = predictionOutputs.get(0);
            assertThat(output).isNotNull();
            Prediction prediction = new Prediction(input, output);

            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            for (Saliency saliency : saliencyMap.values()) {
                assertThat(saliency).isNotNull();
                double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(2));
                assertThat(v).isEqualTo(1d);
            }
            for (int i = 0; i < 10; i++) {
                saliencyMap = limeExplainer.explainAsync(prediction, model)
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            }
        }
    }
}