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
        
        boolean isValidAudio = audio.isMatch() && audio.getConfidence() >= 50.0;
        boolean isValidVideo = video.isMatch() && video.getConfidence() >= 40.0;

        if (isValidAudio && isValidVideo && audio.getMediaId().equals(video.getMediaId())) {
            this.mediaId = audio.getMediaId();
            this.isCrossVerified = true;
            this.statusMessage = "Absolute Match: Both Audio and Video independently verified this media.";
        } else if (isValidAudio) {
            this.mediaId = audio.getMediaId();
            this.isCrossVerified = false;
            this.statusMessage = "Partial Match: Identified via Audio fingerprinting. Video was inconclusive or low confidence.";
            if (!isValidVideo)
                this.videoDetails.match = false;
        } else if (isValidVideo) {
            this.mediaId = video.getMediaId();
            this.isCrossVerified = false;
            this.statusMessage = "Partial Match: Identified via Video fingerprinting. Audio was inconclusive or low confidence.";
            if (!isValidAudio)
                this.audioDetails.match = false;
        } else {
            this.mediaId = "No Match Found";
            this.finalDecision = "No Match Found";
            this.isCrossVerified = false;
            this.statusMessage = "Mismatch: The clip did not meet the minimum confidence thresholds to be considered a match.";
            this.audioDetails.match = false;
            this.videoDetails.match = false;
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