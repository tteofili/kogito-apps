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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Numeric feature distribution based on {@code double[]}.
 */
public class NumericFeatureDistribution implements FeatureDistribution {

    private final Logger LOGGER = LoggerFactory.getLogger(NumericFeatureDistribution.class);

    private final Feature feature;
    private final double[] doubles;

    public NumericFeatureDistribution(Feature feature, double[] doubles) {
        this.feature = feature;
        this.doubles = doubles;
    }

    @Override
    public Feature getFeature() {
        return feature;
    }

    @Override
    public Value<?> sample() {
        return sample(1).get(0);
    }

    @Override
    public List<Value<?>> sample(int sampleSize) {
        if (sampleSize >= doubles.length) {
            LOGGER.warn("required {} samples, but only {} are available", sampleSize, doubles.length);
            return getAllSamples();
        } else {
            List<Double> copy = Arrays.stream(doubles).boxed().collect(Collectors.toList());
            Collections.shuffle(copy);
            List<Value<?>> samples = new ArrayList<>(sampleSize);
            for (int i = 0; i < sampleSize; i++) {
                samples.add(new Value<>(copy.get(i)));
            }
            return samples;
        }
    }

    @Override
    public List<Value<?>> getAllSamples() {
        List<Value<?>> values = new ArrayList<>(doubles.length);
        for (double aDouble : doubles) {
            values.add(new Value<>(aDouble));
        }
        return Collections.unmodifiableList(values);
    }
}