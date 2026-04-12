package com.project.core;

public class MatchResult {
    private String mediaId;
    private String mediaTitle;
    private double confidence;
    private int matchedHashes;
    private int timeOffset;

    public MatchResult(String mediaId, String mediaTitle, double confidence, int matchedHashes, int timeOffset) {
        this.mediaId = mediaId;
        this.mediaTitle = mediaTitle;
        this.confidence = confidence;
        this.matchedHashes = matchedHashes;
        this.timeOffset = timeOffset;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public double getConfidence() {
        return confidence;
    }

    public int getMatchedHashes() {
        return matchedHashes;
    }

    public int getTimeOffset() {
        return timeOffset;
    }

    public boolean isMatch() {
        return !mediaId.equals("No Match Found") && confidence > 0;
    }
}