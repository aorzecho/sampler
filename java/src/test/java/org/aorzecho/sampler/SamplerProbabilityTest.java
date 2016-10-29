package org.aorzecho.sampler;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.hamcrest.number.IsCloseTo.closeTo;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * This test uses simple heuristics (TEST_LOOPS,ERR_RANGE) to verify expected 
 * samples. Should be enough to detect serious flaws and not fail too often with
 * false positives, but TODO: calculate proper values and prove corectness
 * for expected ranges/acuracy.
 * 
 * @author aorzecho
 */
@RunWith(Parameterized.class)
public class SamplerProbabilityTest extends BaseSamplerTest {

    private static final int TEST_LOOPS = 50000;
    private static final double ERR_RANGE = 0.01;

    @Parameter
    public TestCase testCase;

    @Parameters( name = "{index}: {0}" )
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { 
            {TestCase.withInput("abc").sampleSize(3)
                    .probabilityOf('a', 0.3333)
                    .probabilityOf('b', 0.3333)
                    .probabilityOf('c', 0.3333)
            },
            {TestCase.withInput("aaaaaaaaaabbbbbbbbbbcccccccccc").sampleSize(3)
                    .probabilityOf('a', 0.3333)
                    .probabilityOf('b', 0.3333)
                    .probabilityOf('c', 0.3333)
            },
            {TestCase.withInput("abcabcabcbcabcabca").sampleSize(3)
                    .probabilityOf('a', 0.3333)
                    .probabilityOf('b', 0.3333)
                    .probabilityOf('c', 0.3333)
            },
            {TestCase.withInput("abcbccaaaa").sampleSize(5)
                    .probabilityOf('a', 0.5)
                    .probabilityOf('b', 0.2)
                    .probabilityOf('c', 0.3)
            },
            {TestCase.withInput("aaaaa").sampleSize(3)
                    .probabilityOf('a', 1)
                    .probabilityOf('b', 0)
            },
            {TestCase.withInput("ab").sampleSize(3)
                    .probabilityOf('a', 0.5)
                    .probabilityOf('b', 0.5)
            },
            {TestCase.withInput("abcd").sampleSize(3)
                    .probabilityOf('a', 0.25)
                    .probabilityOf('b', 0.25)
                    .probabilityOf('c', 0.25)
                    .probabilityOf('d', 0.25)
            },
        });
    }
    
    @Test
    public void probabilitiesShouldBeAsExpected() throws IOException {
        
        Map<Character, AtomicInteger> counters = new HashMap<>(testCase.expectedProbabilities.size());
        for (Character chr : testCase.expectedProbabilities.keySet()) {
            counters.put(chr, new AtomicInteger());
        }
        
        double total = testCase.sampleSize * TEST_LOOPS;
        
        for (int i = 0; i<TEST_LOOPS; i++) {
            for (Byte b : Sampler.withSampleSize(testCase.sampleSize).sampleStream(withContent(testCase.input)).getSample()) {
                AtomicInteger c = counters.get((char) b.byteValue()); //single byte characters only!
                if (c != null)
                    c.incrementAndGet();
            }
        }
        
        testCase.expectedProbabilities.entrySet().stream().forEach((expected) -> {
            double actual = counters.get(expected.getKey()).doubleValue() / total;
            assertThat("Smth may be wrong... probability of '" + expected.getKey() + "'", actual, closeTo(expected.getValue(), ERR_RANGE));
        });
    }
    
    
    public static class TestCase {
        String input;
        int sampleSize;
        Map<Character, Double> expectedProbabilities = new TreeMap<>();

        static TestCase withInput(String input) {
            TestCase tc = new TestCase();
            tc.input=input;
            return tc;
        }
        
        TestCase sampleSize(int sampleSize) {
            this.sampleSize = sampleSize;
            return this;
        }
        
        TestCase probabilityOf(char chr, double probability) {
            expectedProbabilities.put(chr, probability);
            return this;
        }

        @Override
        public String toString() {
            return "sample " + sampleSize + " from '" + input + "'";
        }
        
    }
}
