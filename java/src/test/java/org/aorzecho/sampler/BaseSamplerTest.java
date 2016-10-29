package org.aorzecho.sampler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Base class for Sampler tests.
 * 
 * @author aorzecho
 */
public abstract class BaseSamplerTest {
    
    /**
     * The current version of the sampler supports only single-byte caractersets
     */
    protected static final Charset THE_CHARSET = StandardCharsets.ISO_8859_1;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    protected InputStream withContent(String content) {
        return new ByteArrayInputStream(content.getBytes(THE_CHARSET));
    }

    protected String asString(byte[] buf) {
        return new String(buf, THE_CHARSET);
    }
    
}
