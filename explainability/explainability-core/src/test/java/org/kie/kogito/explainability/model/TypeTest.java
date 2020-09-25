package org.kie.kogito.explainability.model;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TypeTest {

    @Test
    void testPerturbNumericDouble() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>(0.1);
        Feature f = new Feature("name", Type.NUMBER, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbNumericInteger() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>(1);
        Feature f = new Feature("name", Type.NUMBER, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbSingleTermString() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>("foo");
        Feature f = new Feature("name", Type.TEXT, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbMultiTermString() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>("foo bar");
        Feature f = new Feature("name", Type.TEXT, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbCategorical() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>("1");
        Feature f = new Feature("name", Type.CATEGORICAL, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbBinary() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        ByteBuffer bytes = ByteBuffer.allocate(16);
        bytes.put("foo".getBytes(Charset.defaultCharset()));
        Value<?> value = new Value<>(bytes);
        Feature f = new Feature("name", Type.BINARY, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbBoolean() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>(false);
        Feature f = new Feature("name", Type.BOOLEAN, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbDuration() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>(Duration.ofDays(10));
        Feature f = new Feature("name", Type.DURATION, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbTime() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>(LocalTime.of(10, 10));
        Feature f = new Feature("name", Type.TIME, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbURI() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Value<?> value = new Value<>("http://localhost:8080");
        Feature f = new Feature("name", Type.URI, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbVector() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        double[] doubles = new double[3];
        Arrays.fill(doubles, 1d);
        Value<?> value = new Value<>(doubles);
        Feature f = new Feature("name", Type.VECTOR, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbNestedFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Feature feature = new Feature("name", Type.NUMBER, new Value<>(1d));
        Value<?> value = new Value<>(feature);
        Feature f = new Feature("name", Type.UNDEFINED, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbByteBufferFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        byte[] bytes = new byte[1024];
        perturbationContext.getRandom().nextBytes(bytes);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(bytes);
        Feature feature = new Feature("name", Type.BINARY, new Value<>(byteBuffer));
        Value<?> perturbedValue = feature.getType().perturb(feature.getValue(), perturbationContext);
        assertNotEquals(feature.getValue(), perturbedValue);
    }

    @Test
    void testPerturbURIFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        URI uri = URI.create("https://www.redhat.com/en/technologies/jboss-middleware/process-automation-manager");
        Feature feature = new Feature("name", Type.URI, new Value<>(uri));
        Value<?> perturbedValue = feature.getType().perturb(feature.getValue(), perturbationContext);
        assertNotEquals(feature.getValue(), perturbedValue);
    }

    @Test
    void testPerturbTimeFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        LocalTime time = LocalTime.now();
        Feature feature = new Feature("name", Type.TIME, new Value<>(time));
        Value<?> perturbedValue = feature.getType().perturb(feature.getValue(), perturbationContext);
        assertNotEquals(feature.getValue(), perturbedValue);
    }

    @Test
    void testPerturbDurationFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Duration time = Duration.of(2, ChronoUnit.DAYS);
        Feature feature = new Feature("name", Type.DURATION, new Value<>(time));
        Value<?> perturbedValue = feature.getType().perturb(feature.getValue(), perturbationContext);
        assertNotEquals(feature.getValue(), perturbedValue);
    }

    @Test
    void testPerturbCurrencyFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1);
        Currency currency = Currency.getInstance(Locale.getDefault());
        Feature feature = new Feature("name", Type.CURRENCY, new Value<>(currency));
        Value<?> perturbedValue = feature.getType().perturb(feature.getValue(), perturbationContext);
        assertNotEquals(feature.getValue(), perturbedValue);
    }

    @Test
    void testPerturbCompositeFeature() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 2);
        List<Feature> features = new LinkedList<>();
        features.add(new Feature("f1", Type.TEXT, new Value<>("foo bar")));
        features.add(new Feature("f2", Type.NUMBER, new Value<>(1d)));
        features.add(new Feature("f3", Type.BOOLEAN, new Value<>(true)));
        Value<?> value = new Value<>(features);
        Feature f = new Feature("name", Type.COMPOSITE, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @Test
    void testPerturbCompositeFeatureTooManyPerturbations() {
        PerturbationContext perturbationContext = new PerturbationContext(new Random(), 1000);
        List<Feature> features = new LinkedList<>();
        features.add(new Feature("f1", Type.TEXT, new Value<>("foo bar")));
        features.add(new Feature("f2", Type.NUMBER, new Value<>(1d)));
        Value<?> value = new Value<>(features);
        Feature f = new Feature("name", Type.COMPOSITE, value);
        Value<?> perturbedValue = f.getType().perturb(f.getValue(), perturbationContext);
        assertNotEquals(value, perturbedValue);
    }

    @ParameterizedTest
    @EnumSource
    void testDrop(Type type) {
        Value<?> v = new Value<>(1.0);
        Value<?> dropped = type.drop(v);
        assertNotEquals(v, dropped);
    }

    @ParameterizedTest
    @EnumSource
    void testPerturb(Type type) {
        for (int seed = 0; seed < 5; seed++) {
            Value<?> v = new Value<>(1.0);
            Random random = new Random();
            random.setSeed(seed);
            PerturbationContext perturbationContext = new PerturbationContext(random, 1);
            Value<?> perturbed = type.perturb(v, perturbationContext);
            assertNotEquals(v, perturbed, type.name());
        }
    }
}