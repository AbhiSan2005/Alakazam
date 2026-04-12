package com.project.fingerprint;

public class FFTStrategy {

    public static void fft(double[] real, double[] imag) {
        int n = real.length;

        if (n == 1) return;

        if ((n & (n - 1)) != 0) {
            throw new IllegalArgumentException("Size must be power of 2");
        }

        // divide
        double[] evenReal = new double[n / 2];
        double[] evenImag = new double[n / 2];
        double[] oddReal = new double[n / 2];
        double[] oddImag = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            evenReal[i] = real[2 * i];
            evenImag[i] = imag[2 * i];
            oddReal[i] = real[2 * i + 1];
            oddImag[i] = imag[2 * i + 1];
        }

        // conquer
        fft(evenReal, evenImag);
        fft(oddReal, oddImag);

        // combine
        for (int k = 0; k < n / 2; k++) {
            double angle = -2 * Math.PI * k / n;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double treal = cos * oddReal[k] - sin * oddImag[k];
            double timag = sin * oddReal[k] + cos * oddImag[k];

            real[k] = evenReal[k] + treal;
            imag[k] = evenImag[k] + timag;
            real[k + n / 2] = evenReal[k] - treal;
            imag[k + n / 2] = evenImag[k] - timag;
        }
    }
}
