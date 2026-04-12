package com.project.core;

public class UnifiedMatchResponse {
    public String finalDecision;
    public String mediaId;
    public String genre;
    public int duration;
    public int yearOfRelease;

    public boolean isCrossVerified;
    public String statusMessage;

    public PipelineDetails audioDetails;
    public PipelineDetails videoDetails;

    public UnifiedMatchResponse(MatchResult audio, MatchResult video) {
        this.audioDetails = new PipelineDetails(audio.getConfidence(), audio.getMatchedHashes(), audio.getTimeOffset(),
                audio.isMatch());
        this.videoDetails = new PipelineDetails(video.getConfidence(), video.getMatchedHashes(), video.getTimeOffset(),
                video.isMatch());
        this.evaluateConsensus(audio, video);
    }

    private void evaluateConsensus(MatchResult audio, MatchResult video) {
        boolean audioMatched = audio.isMatch();
        boolean videoMatched = video.isMatch();

        if (audioMatched && videoMatched && audio.getMediaId().equals(video.getMediaId())) {
            this.mediaId = audio.getMediaId();
            this.isCrossVerified = true;
            this.statusMessage = "Absolute Match: Both Audio and Video independently verified this media.";
        } else if (audioMatched) {
            this.mediaId = audio.getMediaId();
            this.isCrossVerified = false;
            this.statusMessage = "Partial Match: Identified via Audio fingerprinting. Video was inconclusive.";
        } else if (videoMatched) {
            this.mediaId = video.getMediaId();
            this.isCrossVerified = false;
            this.statusMessage = "Partial Match: Identified via Video fingerprinting. Audio was inconclusive.";
        } else {
            this.mediaId = "No Match Found";
            this.finalDecision = "No Match Found";
            this.isCrossVerified = false;
            this.statusMessage = "Mismatch: Neither pipeline could identify this clip.";
        }
    }

    public void applyMetadata(MovieMetaData meta) {
        if (meta != null) {
            this.finalDecision = meta.title;
            this.genre = meta.genre;
            this.duration = meta.duration;
            this.yearOfRelease = meta.yearOfRelease;
        }
    }

    public static class PipelineDetails {
        public double confidence;
        public int matchedHashes;
        public int timeOffset;
        public boolean match;

        public PipelineDetails(double confidence, int matchedHashes, int timeOffset, boolean match) {
            this.confidence = confidence;
            this.matchedHashes = matchedHashes;
            this.timeOffset = timeOffset;
            this.match = match;
        }
    }
}