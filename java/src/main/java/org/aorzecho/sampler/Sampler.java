package org.aorzecho.sampler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 *
 * @author aorzecho
 */
public class Sampler {

    private final byte[] reservoir;
    private final int bufferSize;
    int idx;

    Sampler(int sampleSize) {
        String sbs = System.getenv("S_BUFFER_SIZE");
        bufferSize = sbs != null ? Integer.parseInt(sbs) : 1024;
        this.reservoir = new byte[sampleSize];
        this.idx = sampleSize;
    }

    int sampleStream(InputStream in) throws IOException {
        Random random = new Random();
        int sampleSize = reservoir.length;
        if (bufferSize > 1) {
            in = new BufferedInputStream(in);
        }

        in.read(reservoir);

        for (int next = in.read(); next >= 0; next = in.read(), idx++) {
            int j = random.nextInt(idx);
            if (j < sampleSize) {
                reservoir[j] = (byte) next;
            }
        }
        return idx;
    }

    byte[] getSample() {
        return reservoir;
    }

    public static void main(String[] args) throws IOException {
        int sampleSize = Integer.parseInt(args[0]);
        Sampler s = new Sampler(sampleSize);
        s.sampleStream(System.in);
        System.out.printf("Random Sample: %s\n", new String(s.getSample()));
    }
}
