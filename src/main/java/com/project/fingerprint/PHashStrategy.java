package com.project.fingerprint;

import com.project.core.Fingerprint;
import com.project.core.FrameFingerprint;
import com.project.core.VideoFingerprint;
import com.project.media.Media;
import com.project.utils.VideoFrameGrabber;
import com.project.utils.VideoFrameHasher;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.util.ArrayList;
import java.util.List;

public class PHashStrategy implements FingerprintStrategy {

    //grabs frame at
    @Override
    public VideoFingerprint generate(Media media) {
        List<FrameFingerprint> frames = new ArrayList<>();
        VideoFrameHasher frameHasher = new VideoFrameHasher();
//        int cnt = 0;
        try (VideoFrameGrabber grabber = new VideoFrameGrabber(media.getFile())){
            long length = grabber.getLengthInTime();
            for (long i = 1_000_000L; i < length; i += 1_000_000L) {
                Frame frame = grabber.grabFrameAtTimestamp(i);
//                System.out.print("Frame: " + ++cnt);
//                System.out.println(" Hash: " + frameHasher.hashFrame(frame));
                frames.add(new FrameFingerprint(media.getId(), (int) (i / 1_000_000), frameHasher.hashFrame(frame), (int) i / (5 * 1_000_000)));
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        return new VideoFingerprint(frames);
    }
}
