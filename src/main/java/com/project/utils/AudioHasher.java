package com.project.utils;

import com.project.core.FrameFingerprint;
import com.project.core.AudioFingerprint;
import com.project.fingerprint.FFT;

import java.util.ArrayList;
import java.util.List;

public class AudioHasher {

    private static final int WINDOW_SIZE = 1024;
    private static final int OVERLAP = 512;
    private static final int[] BANDS = {40, 80, 120, 180, 300};

    public static AudioFingerprint generate(String mediaId, double[] samples) {
        List<int[]> constellationMap = new ArrayList<>();
        int offset = 0;

        // 1. Sliding Window & FFT
        while (offset + WINDOW_SIZE < samples.length) {
            double[] real = new double[WINDOW_SIZE];
            double[] imag = new double[WINDOW_SIZE];

            for (int i = 0; i < WINDOW_SIZE; i++) {
                real[i] = samples[offset + i];
                imag[i] = 0;
            }

            FFT.fft(real, imag);

            double[] magnitude = new double[WINDOW_SIZE / 2];
            for (int i = 0; i < magnitude.length; i++) {
                magnitude[i] = Math.log(1 + Math.sqrt(real[i] * real[i] + imag[i] * imag[i]));
            }

            constellationMap.add(getPeaks(magnitude));
            offset += OVERLAP;
        }

        // 2. Combinatorial Hashing
        List<FrameFingerprint> dbHashes = new ArrayList<>();
        int targetZoneSize = 5; 

        for (int anchorTime = 0; anchorTime < constellationMap.size() - targetZoneSize; anchorTime++) {
            int[] anchorPeaks = constellationMap.get(anchorTime);

            for (int aPeak : anchorPeaks) {
                if (aPeak == 0) continue;

                for (int tOffset = 1; tOffset <= targetZoneSize; tOffset++) {
                    int[] targetPeaks = constellationMap.get(anchorTime + tOffset);

                    for (int tPeak : targetPeaks) {
                        if (tPeak == 0) continue;

                        long hash = ((long) aPeak) | (((long) tPeak) << 16) | (((long) tOffset) << 32);

                        dbHashes.add(new FrameFingerprint(
                                mediaId,
                                anchorTime, 
                                hash,
                                0
                        ));
                    }
                }
            }
        }
        
        // Return the properly named object
        return new AudioFingerprint(dbHashes);
    }

    private static int[] getPeaks(double[] mag) {

        int[] peaks = new int[BANDS.length - 1];

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

    // private static int hash(int[] peaks) {
    //     // compress peaks into single int
    //     return peaks[0] * 100000
    //          + peaks[1] * 1000
    //          + peaks[2] * 10
    //          + peaks[3];
    // }
}
