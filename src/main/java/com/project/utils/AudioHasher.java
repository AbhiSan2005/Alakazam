package com.project.utils;

import com.project.core.AudioFingerprint;
import com.project.fingerprint.FFT;

import java.util.*;

public class AudioHasher {

    private static final int WINDOW_SIZE = 1024;
    private static final int OVERLAP = 512;

    // Frequency bands (Shazam-style simplification)
    private static final int[] BANDS = {40, 80, 120, 180, 300};

    public static AudioFingerprint generate(int[] samples) {
        AudioFingerprint fingerprint = new AudioFingerprint();

        int offset = 0;

        while (offset + WINDOW_SIZE < samples.length) {

            double[] real = new double[WINDOW_SIZE];
            double[] imag = new double[WINDOW_SIZE];

            // copy samples
            for (int i = 0; i < WINDOW_SIZE; i++) {
                real[i] = samples[offset + i];
                imag[i] = 0;
            }

            // FFT
            FFT.fft(real, imag);

            // Magnitude
            double[] magnitude = new double[WINDOW_SIZE / 2];
            for (int i = 0; i < magnitude.length; i++) {
                magnitude[i] = Math.log(1 + Math.sqrt(real[i]*real[i] + imag[i]*imag[i]));
            }

            // Peak picking
            int[] peaks = getPeaks(magnitude);

            // Hash generation
            int hash = hash(peaks);

            fingerprint.addHash(hash, offset);

            offset += OVERLAP;
        }

        return fingerprint;
    }

    private static int[] getPeaks(double[] mag) {
        int[] peaks = new int[BANDS.length];

        for (int b = 0; b < BANDS.length - 1; b++) {
            int start = BANDS[b];
            int end = BANDS[b + 1];

            double max = -1;
            int index = start;

            for (int i = start; i < end && i < mag.length; i++) {
                if (mag[i] > max) {
                    max = mag[i];
                    index = i;
                }
            }

            peaks[b] = index;
        }

        return peaks;
    }

    private static int hash(int[] peaks) {
        // compress peaks into single int
        return peaks[0] * 100000
             + peaks[1] * 1000
             + peaks[2] * 10
             + peaks[3];
    }
}
