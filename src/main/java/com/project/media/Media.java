package com.project.media;
import com.project.core.Fingerprint;
import com.project.fingerprint.Fingerprintable;

import java.io.File;

public abstract class Media implements Fingerprintable {
    private String id;
    private String title;
    private File file;

    public Media(String id, String title, File file) {
        this.id = id;
        this.title = title;
        this.file = file;
    }

    //Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public File getFile() {
        return file;
    }

    @Override
    public abstract Fingerprint generateFingerprint();


}