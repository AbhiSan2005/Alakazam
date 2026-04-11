package com.project.core;

import java.util.*;

public class AudioFingerprint extends Fingerprint {

    // hash -> list of time offsets
    private Map<Integer, List<Integer>> hashMap;

    public AudioFingerprint() {
        this.hashMap = new HashMap<>();
    }

    public void addHash(int hash, int timeOffset) {
        hashMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(timeOffset);
    }

    public Map<Integer, List<Integer>> getHashMap() {
        return hashMap;
    }
}
