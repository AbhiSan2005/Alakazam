package com.project.fingerprint;
import com.project.core.Fingerprint;
import com.project.media.Media;

public interface FingerprintStrategy {
    Fingerprint generate(Media samples);
}