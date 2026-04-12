package com.project.core;

public class FrameFingerprint{
    private final String mediaID;
    private final int timestamp;
    private final long hash;
    private final int chunkID;

    public FrameFingerprint(String mediaID, int timestamp, long hash, int chunkID) {
        this.mediaID = mediaID;
        this.timestamp = timestamp;
        this.hash = hash;
        this.chunkID = chunkID;
    }

    public String getMediaID() {
        return mediaID;
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
                "mediaID='" + mediaID + '\'' +
                ", timestamp=" + timestamp +
                ", hash=" + hash +
                ", chunkID=" + chunkID +
                '}';
    }
}
