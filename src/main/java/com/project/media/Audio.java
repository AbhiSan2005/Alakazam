package com.project.media;

import com.project.core.AudioFingerprint;
import com.project.utils.AudioFrameGrabber;
import com.project.utils.AudioHasher;

import java.io.File;

public class Audio extends Media {

    public Audio(String id, String title, File audio) {
        super(id, title, audio);
    }

    public Audio(String id, String title, String audioPath) {
        super(id, title, new File(audioPath));
    }

    @Override
    public AudioFingerprint generateFingerprint() {
        try (AudioFrameGrabber grabber = new AudioFrameGrabber(getFile())) {
            

            double[] samples = grabber.extractAllSamples();
            
            return AudioHasher.generate(getId(), samples);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate audio fingerprint", e);
        } finally {
            org.bytedeco.javacpp.Pointer.deallocateReferences();
        }
    }
}