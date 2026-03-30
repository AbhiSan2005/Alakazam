package com.project.core;

import java.util.List;

public class VideoFingerprint extends Fingerprint{
    private List<FrameFingerprint> frames;
    private String videoId;
    private String videoTitle;


    public VideoFingerprint(List<FrameFingerprint> frames) {
        this.frames = frames;
    }

    public List<FrameFingerprint> getFrames() {
        return frames;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }
}
