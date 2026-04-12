package com.project.fingerprint;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import com.project.core.FrameFingerprint;
import com.project.core.VideoFingerprint;
import com.project.media.Media;
import com.project.utils.VideoFrameGrabber;
import com.project.utils.VideoFrameHasher;

public class PHashStrategy implements FingerprintStrategy {

    @Override
    public VideoFingerprint generate(Media media) {
        List<FrameFingerprint> frames = new ArrayList<>();
        VideoFrameHasher frameHasher = new VideoFrameHasher();
        try (VideoFrameGrabber grabber = new VideoFrameGrabber(media.getFile())){
            long length = grabber.getLengthInTime();
            for (long i = 1_000_000L; i < length; i += 1_000_000L) {
                Frame frame = grabber.grabFrameAtTimestamp(i);
                int timestampInSec = (int) (i / 1_000_000);
                frames.add(new FrameFingerprint(media.getId(), timestampInSec, frameHasher.hashFrame(frame), timestampInSec / 5));
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        return new VideoFingerprint(frames);
    }
}
