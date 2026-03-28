package com.project.media;
import com.project.core.Fingerprint;
import com.project.fingerprint.Fingerprintable;

public abstract class Media implements Fingerprintable {
    private String id;
    private String title;

    public Media(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }

    @Override
    public abstract Fingerprint generateFingerprint();
}