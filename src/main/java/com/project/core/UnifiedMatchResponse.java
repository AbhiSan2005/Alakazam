package com.project.core;

public class UnifiedMatchResponse {
    public String finalDecision;
    public boolean isCrossVerified;
    public String statusMessage;
    
    public MatchResult audioDetails;
    public MatchResult videoDetails;

    public UnifiedMatchResponse(MatchResult audio, MatchResult video) {
        this.audioDetails = audio;
        this.videoDetails = video;
        this.evaluateConsensus();
    }

    private void evaluateConsensus() {
        boolean audioMatched = audioDetails.isMatch();
        boolean videoMatched = videoDetails.isMatch();

        // 1. BEST CASE: Both pipelines found the exact same movie ID
        if (audioMatched && videoMatched && audioDetails.getMediaId().equals(videoDetails.getMediaId())) {
            this.finalDecision = audioDetails.getMediaTitle();
            this.isCrossVerified = true;
            this.statusMessage = "Absolute Match: Both Audio and Video independently verified this media.";
        } 
        // 2. AUDIO ONLY
        else if (audioMatched) {
            this.finalDecision = audioDetails.getMediaTitle();
            this.isCrossVerified = false;
            this.statusMessage = "Partial Match: Identified via Audio fingerprinting. Video was inconclusive.";
        } 
        // 3. VIDEO ONLY
        else if (videoMatched) {
            this.finalDecision = videoDetails.getMediaTitle();
            this.isCrossVerified = false;
            this.statusMessage = "Partial Match: Identified via Video fingerprinting. Audio was inconclusive.";
        } 
        // 4. WORST CASE
        else {
            this.finalDecision = "No Match Found";
            this.isCrossVerified = false;
            this.statusMessage = "Mismatch: Neither pipeline could identify this clip.";
        }
    }
}