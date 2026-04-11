package com.project.media;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.project.core.AudioFingerprint;
import com.project.utils.AudioReader;
import com.project.utils.AudioHasher;

public class Audio extends Media{
    //private String audioId;
    //private String audioTitle;
    //private File audio;

    public Audio(String id, String title, File audio) {
        super(id, title, audio);
        //this.videoId = id;
        //this.videoTitle = title;
        //this.video = video;
    }

    public Audio(String id, String title, String audioPath) {
        super(id, title, new File(audioPath));
        //this.audio = new File(audioPath);
    }

    @Override
    public AudioFingerprint generateFingerprint() {
        try {
            int[] samples = AudioReader.read(getFile().getPath());
            return AudioHasher.generate(samples);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate fingerprint", e);
        }
    }

    //Getters
    public String getAudioId() {
        return super.getId();
    }

    public String getAudioTitle() {
        return super.getTitle();
    }

    public File getAudio() {
        return super.getFile();
    }

}
