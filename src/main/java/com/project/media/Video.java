package com.project.media;

import com.project.core.Fingerprint;
import com.project.core.FrameFingerprint;
import com.project.core.VideoFingerprint;
import com.project.fingerprint.PHashStrategy;
import com.project.utils.VideoFrameGrabber;
import com.project.utils.VideoFrameHasher;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Video extends Media{
    private String videoId;
    private String videoTitle;
    private File video;

    public Video(String id, String title, File video) {
        super(id, title, video);
        this.videoId = id;
        this.videoTitle = title;
        this.video = video;
    }

    public Video(String id, String title, String videoPath) {
        super(id, title, new File(videoPath));
        this.video = new File(videoPath);
    }

    @Override
    public VideoFingerprint generateFingerprint() {
        var strategy = new PHashStrategy();
        return strategy.generate(this);
    }

    //Getters
    public String getVideoId() {
        return videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public File getVideo() {
        return video;
    }

    //Old implementation: prints Frame <No>: <Hash>
    public void videoHasher() {
        VideoFrameHasher frameHasher = new VideoFrameHasher();
        int cnt = 0;
        try (VideoFrameGrabber grabber = new VideoFrameGrabber(video)){
            long length = grabber.getLengthInTime();
            for (long i = 1_000_000L; i < length; i += 1_000_000L) {
                Frame frame = grabber.grabFrameAtTimestamp(i);
                System.out.print("Frame: " + ++cnt);
                System.out.println(" Hash: " + frameHasher.hashFrame(frame));
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }
}