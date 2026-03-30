package com.project.core;

public class FrameFingerprint{
    private final String videoID;
    private final int timestamp;
    private final long hash;
    private final int chunkID;

    public FrameFingerprint(String videoID, int timestamp, long hash, int chunkID) {
        this.videoID = videoID;
        this.timestamp = timestamp;
        this.hash = hash;
        this.chunkID = chunkID;
    }

    public String getVideoID() {
        return videoID;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public long getHash() {
        return hash;
    }

    public int getChunkID() {
        return chunkID;
    }

    @Override
    public String toString() {
        return "FrameFingerprint{" +
                "videoID='" + videoID + '\'' +
                ", timestamp=" + timestamp +
                ", hash=" + hash +
                ", chunkID=" + chunkID +
                '}';
    }
}
