package org.aorzecho.sampler;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * These tests focus on the contract/usage of the {@link Sampler} class. For 
 * tests checking the algorithm see {@link SamplerProbabilityTest}.
 * 
 * @author aorzecho
 */
public class SamplerTest extends BaseSamplerTest {
    
    
    @Test
    public void streamOfSameCharactersShouldProduceSampleOfSameCharacters() throws Exception {
        byte[] result = Sampler.withSampleSize(3).sampleStream(withContent("aaaaaaaaaa")).getSample();
        assertEquals("aaa", asString(result));
    }

    @Test
    public void streamShorterThanSampleShouldProduceFullSample() throws Exception {
        byte[] result = Sampler.withSampleSize(3).sampleStream(withContent("a")).getSample();
        assertEquals("aaa", asString(result));
    }

    @Test
    public void emptyStreamShouldProduceEmptySample() throws Exception {
        byte[] result = Sampler.withSampleSize(3).sampleStream(withContent("")).getSample();
        assertEquals("", asString(result));
    }
    
    @Test
    public void samplingMultipleStreamsShouldWork() throws Exception {
        byte[] result = Sampler.withSampleSize(3)
                .sampleStream(withContent(""))
                .sampleStream(withContent("a"))
                .sampleStream(withContent("aaa"))
                .getSample();
        assertEquals("aaa", asString(result));
    }
    
    @Test
    public void samplingNullStreamShouldThrowNPEWithMessage() throws Exception {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage(containsString("sample"));
        Sampler.withSampleSize(3).sampleStream(null);
    }
    
    @Test
    public void negativeSampleSizeShouldThrowException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage(containsString("sample size"));
        Sampler.withSampleSize(-2);
    }
    
    @Test
    public void zeroSampleSizeShouldThrowException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage(containsString("sample size"));
        Sampler.withSampleSize(0);
    }
    
    
}
