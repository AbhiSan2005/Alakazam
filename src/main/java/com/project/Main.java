package com.project;

import com.project.core.AudioFingerprint;
import com.project.core.VideoFingerprint;
import com.project.media.Audio;
import com.project.media.Video;

public class Main {
    public static void main(String[] args) {
        //Video video = new Video("videoid","videotitle","assets/video/Test 2/Test Video 2.mp4");
        //var videoFingerprint = video.generateFingerprint();
        //System.out.println(videoFingerprint.getFrames());
        
        Audio audio = new Audio("id", "testSong", "D:\\Alakazam\\assets\\video\\Test 1\\Test Video 1.mp4");
        var audioFingerprint = audio.generateFingerprint();
        System.out.println("✅ Audio Hashing Successful!");
        System.out.println("Total Audio Hashes Generated: " + audioFingerprint.getFrames().size());
        //System.out.println(audioFingerprint.getHashMap());
        //Old implementation
//        video.videoHasher();
    }
}
