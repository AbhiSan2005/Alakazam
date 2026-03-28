package com.project.fingerprint;
import com.project.core.Fingerprint;

public interface FingerprintStrategy {
    Fingerprint generate(double[] samples);
}