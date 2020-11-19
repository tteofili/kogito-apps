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
package org.kie.kogito.explainability.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.utils.DataUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IndependentFeaturesDatatDistributionTest {

    @Test
    void testSample() {
        List<FeatureDistribution> featureDistributions = new ArrayList<>(3);
        double[] doubles1 = DataUtils.generateSamples(0, 1, 10);
        double[] doubles2 = DataUtils.generateSamples(0, 1, 10);
        double[] doubles3 = DataUtils.generateSamples(0, 1, 10);
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles1));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles2));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles3));
        IndependentFeaturesDatatDistribution independentFeaturesDatatDistribution = new IndependentFeaturesDatatDistribution(featureDistributions);
        PredictionInput sample = independentFeaturesDatatDistribution.sample();
        assertNotNull(sample);
        assertEquals(3, sample.getFeatures().size());
        assertThat(sample.getFeatures().get(0).getValue().asNumber()).isBetween(0d, 1d);
        assertThat(sample.getFeatures().get(1).getValue().asNumber()).isBetween(0d, 1d);
        assertThat(sample.getFeatures().get(2).getValue().asNumber()).isBetween(0d, 1d);
    }

    @Test
    void testSamples() {
        List<FeatureDistribution> featureDistributions = new ArrayList<>(3);
        double[] doubles1 = DataUtils.generateSamples(0, 1, 10);
        double[] doubles2 = DataUtils.generateSamples(0, 1, 10);
        double[] doubles3 = DataUtils.generateSamples(0, 1, 10);
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles1));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles2));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles3));
        IndependentFeaturesDatatDistribution independentFeaturesDatatDistribution = new IndependentFeaturesDatatDistribution(featureDistributions);
        List<PredictionInput> samples = independentFeaturesDatatDistribution.sample(3);
        assertNotNull(samples);
        assertEquals(3, samples.size());
        for (PredictionInput sample : samples) {
            assertThat(sample.getFeatures().get(0).getValue().asNumber()).isBetween(0d, 1d);
            assertThat(sample.getFeatures().get(1).getValue().asNumber()).isBetween(0d, 1d);
            assertThat(sample.getFeatures().get(2).getValue().asNumber()).isBetween(0d, 1d);
        }
    }

    @Test
    void testGetAllSamples() {
        List<FeatureDistribution> featureDistributions = new ArrayList<>(3);
        double[] doubles1 = DataUtils.generateSamples(0, 1, 3);
        double[] doubles2 = DataUtils.generateSamples(0, 1, 3);
        double[] doubles3 = DataUtils.generateSamples(0, 1, 3);
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles1));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles2));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles3));
        IndependentFeaturesDatatDistribution independentFeaturesDatatDistribution = new IndependentFeaturesDatatDistribution(featureDistributions);
        List<PredictionInput> samples = independentFeaturesDatatDistribution.getAllSamples();
        assertNotNull(samples);
        assertEquals(27, samples.size());
        for (PredictionInput sample : samples) {
            assertThat(sample.getFeatures().get(0).getValue().asNumber()).isBetween(0d, 1d);
            assertThat(sample.getFeatures().get(1).getValue().asNumber()).isBetween(0d, 1d);
            assertThat(sample.getFeatures().get(2).getValue().asNumber()).isBetween(0d, 1d);
        }
    }

    @Test
    void testAsFeatureDistributions() {
        List<FeatureDistribution> featureDistributions = new ArrayList<>(3);
        double[] doubles1 = DataUtils.generateSamples(0, 1, 3);
        double[] doubles2 = DataUtils.generateSamples(0, 1, 3);
        double[] doubles3 = DataUtils.generateSamples(0, 1, 3);
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles1));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles2));
        featureDistributions.add(new NumericFeatureDistribution(TestUtils.getMockedNumericFeature(), doubles3));
        IndependentFeaturesDatatDistribution independentFeaturesDatatDistribution = new IndependentFeaturesDatatDistribution(featureDistributions);
        List<FeatureDistribution> list = independentFeaturesDatatDistribution.asFeatureDistributions();
        assertEquals(list, featureDistributions);
    }
}