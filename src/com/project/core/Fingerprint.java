package com.project.core;
import java.util.List;

public class Fingerprint {
    private List<Integer> features;

    public Fingerprint(List<Integer> features) {
        this.features = features;
    }

    public List<Integer> getFeatures() { return features; }

    public double compare(Fingerprint other) {
        //Will do later
        return 0.0;
    }
}