package com.project.media;

import com.project.core.AudioFingerprint;
import com.project.fingerprint.FFTStrategy;

import java.io.File;

public class Audio extends Media {

    public Audio(String id, String title, File audio) {
        super(id, title, audio);
    }

    public Audio(String id, String title, String audioPath) {
        super(id, title, new File(audioPath));
    }

    @Override
    public AudioFingerprint generateFingerprint() {
        var strategy = new FFTStrategy();
        return strategy.generate(this);
    }
}