package org.aorzecho.sampler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Stream sampler generating random representative sample of given size.
 * <p>
 * The implementation uses a version of Algorithm R, as described by Jeffrey Vitter
 * in[1], to calculate the sample during reading the stream. 
 * <p>
 * The byte array for the sample is allocated at initialization time. The memory
 * usage does not depend on the size of the stream, but for more efficient reading
 * the stream is buffered with default buffer size of 1024. The buffer size can
 * be modified (or disabled by setting to 0) using "S_BUFFER_SIZE" environment
 * variable.
 * <p>
 * Limitations/TODOS:
 * <li>the sampler works on byte[] and so supports only single byte charactersets,
 * supporting unicode with Basic Multilingual Plane (BMP) will require using a
 * char array(double the memory) and also impact performance ({@link Reader}), 
 * support for unicode supplementary characters introduces additional complexity 
 * of variable size of the "token"
 * <li>theoretical max length of the stream is Long.MAX_VALUE (~exabyte)
 * <li> java {@link Random} uses 48bit seed and so may not produce all possible long
 * values, that should not matter in practice as long as the values are uniformly 
 * distributed, alternatively use {@link java.security.SecureRandom} with possibly
 * much worse performance.
 * <li>the implementation is single threaded thus for fast streams cpu becomes
 * the bottleneck
 * <p>
 * The implementation is not thread safe.
 * 
 * @see  [1]Vitter, Jeffrey S. (1 March 1985). <a href="http://www.cs.umd.edu/~samir/498/vitter.pdf">Random sampling with a reservoir" (PDF).</a> ACM Transactions on Mathematical Software. 11 (1): 37–57. doi:10.1145/3147.3165
 * @author aorzecho
 */
public class Sampler {

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final Logger logger = Logger.getLogger(Sampler.class.getName());
    
    private final byte[] reservoir;
    private final int bufferSize;
    long streamIndex;

    public static Sampler withSampleSize(int samplesize) {
        return new Sampler(samplesize);
    }

    private Sampler(int sampleSize) {
        String sbs = System.getenv("S_BUFFER_SIZE");
        bufferSize = sbs != null ? Integer.parseInt(sbs) : DEFAULT_BUFFER_SIZE; //TODO: handle parsing exceptions
        if (sampleSize <=0)
            throw new IllegalArgumentException("Invalid sample size: " + sampleSize);
        this.reservoir = new byte[sampleSize];
    }

    public Sampler sampleStream(InputStream in) throws IOException {

        Objects.requireNonNull(in, "Cannot sample null stream");
        int sampleSize = reservoir.length;
        if (bufferSize > 1) {
            in = new BufferedInputStream(in, bufferSize);
        }

        streamIndex = in.read(reservoir);

        for (int next = in.read(); next >= 0; next = in.read()) {
            streamIndex++;
            long j = ThreadLocalRandom.current().nextLong(streamIndex);
            if (j < sampleSize) {
                reservoir[(int) j] = (byte) next;
            }
        }
        return this;
    }

    public byte[] getSample() {
        if (streamIndex <= 0) {
            return new byte[0];
        }
        if (streamIndex < reservoir.length) {
            fillUpReservoir();
        }
        return reservoir;
    }

    private void fillUpReservoir() {
        for (long i = streamIndex; i < reservoir.length; i++) {
            reservoir[(int) i] = reservoir[ThreadLocalRandom.current().nextInt((int) streamIndex)]; //safe to cast here as streamIndex < reservoir.length (which is int)
        }
    }

    public static void main(String[] args) throws IOException {
        int sampleSize = Integer.parseInt(args[0]);
        System.out.printf("Random Sample: %s%n",
                new String(
                        Sampler.withSampleSize(sampleSize)
                        .sampleStream(System.in)
                        .getSample(),
                        StandardCharsets.ISO_8859_1
                )
        );
    }

}
