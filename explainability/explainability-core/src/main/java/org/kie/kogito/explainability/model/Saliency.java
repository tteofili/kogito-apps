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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * The saliency generated by an explanation algorithm.
 * A saliency contains a feature importance for each explained feature.
 */
public class Saliency {

    private final Output output;
    private final List<FeatureImportance> perFeatureImportance;

    public Saliency(Output output, List<FeatureImportance> perFeatureImportance) {
        this.output = output;
        this.perFeatureImportance = Collections.unmodifiableList(perFeatureImportance);
    }

    public List<FeatureImportance> getPerFeatureImportance() {
        return perFeatureImportance;
    }

    public Output getOutput() {
        return output;
    }

    public List<FeatureImportance> getTopFeatures(int k) {
        return perFeatureImportance.stream().sorted((f0, f1) -> Double.compare(
                Math.abs(f1.getScore()), Math.abs(f0.getScore()))).limit(k).collect(Collectors.toList());
    }

    public List<FeatureImportance> getPositiveFeatures(int k) {
        return perFeatureImportance.stream().sorted((f0, f1) -> Double.compare(
                Math.abs(f1.getScore()), Math.abs(f0.getScore()))).filter(f -> f.getScore() >= 0).limit(k).collect(Collectors.toList());
    }

    public List<FeatureImportance> getNegativeFeatures(int k) {
        return perFeatureImportance.stream().sorted((f0, f1) -> Double.compare(
                Math.abs(f1.getScore()), Math.abs(f0.getScore()))).filter(f -> f.getScore() < 0).limit(k).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Saliency{" +
                "output=" + output +
                ", perFeatureImportance=" + perFeatureImportance +
                '}';
    }

    /**
     * Merge saliencies so that they are aggregated by {@code Output} and the resulting list of associated
     * {@code FeatureImportance}s is derived by calculating the mean score for each feature across all such saliencies.
     *
     * @param saliencies a collection of saliency maps
     * @return a map of merged saliencies, one for each output appearing in the input saliencies
     */
    public static Map<String, Saliency> merge(Collection<Map<String, Saliency>> saliencies) {
        Map<String, Saliency> finalResult = new HashMap<>();

        // group Saliencies by Output
        Map<Output, List<Saliency>> flatten = saliencies.stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Saliency::getOutput));

        // calculate mean feature importance
        for (Map.Entry<Output, List<Saliency>> saliencyEntry : flatten.entrySet()) {
            List<FeatureImportance> result = new ArrayList<>();
            List<FeatureImportance> fis = saliencyEntry.getValue().stream().map(s -> s.perFeatureImportance).flatMap(Collection::stream).collect(Collectors.toList());
            Map<Feature, List<FeatureImportance>> collect = fis.stream().collect(Collectors.groupingBy(fi -> FeatureFactory.copyOf(fi.getFeature(), new Value<>(null))));
            for (Map.Entry<Feature, List<FeatureImportance>> entry : collect.entrySet()) {
                double meanScore = entry.getValue().stream().map(FeatureImportance::getScore).flatMapToDouble(DoubleStream::of).average().orElse(0);
                result.add(new FeatureImportance(entry.getKey(), meanScore));
            }
            result.sort(Comparator.comparing(f -> f.getFeature().getName()));
            finalResult.put(saliencyEntry.getKey().getName(), new Saliency(saliencyEntry.getKey(), result));
        }
        return finalResult;
    }

}