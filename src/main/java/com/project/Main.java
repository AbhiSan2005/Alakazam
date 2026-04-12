package com.project;

// import com.project.core.VideoFingerprint;
import com.project.media.Video;

public class Main {
    public static void main(String[] args) {
        Video video = new Video("videoid","videotitle","assets/video/Test 2/Test Video 2.mp4");
        var videoFingerprint = video.generateFingerprint();
        System.out.println(videoFingerprint.getFrames());

        //Old implementation
//        video.videoHasher();
    }
}