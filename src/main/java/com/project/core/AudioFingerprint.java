package com.project.core;

import java.util.ArrayList;
import java.util.List;

public class AudioFingerprint extends Fingerprint {

    private List<FrameFingerprint> frames;

    public AudioFingerprint() {
        this.frames = new ArrayList<>();
    }

    public AudioFingerprint(List<FrameFingerprint> frames) {
        this.frames = frames;
    }

    public void addHash(FrameFingerprint frame) {
        this.frames.add(frame);
    }

    public List<FrameFingerprint> getFrames() {
        return frames;
    }
}