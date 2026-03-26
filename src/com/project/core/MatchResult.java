package com.project.core;

public class MatchResult {
    private String mediaId;
    private double confidence;

    public MatchResult(String mediaId, double confidence) {
        this.mediaId = mediaId;
        this.confidence = confidence;
    }

    public String getMediaId() { return mediaId; }
    public double getConfidence() { return confidence; }
}