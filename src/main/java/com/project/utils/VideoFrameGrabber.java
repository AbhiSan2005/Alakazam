package com.project.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
// import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

//grab and save frames to image files
public class VideoFrameGrabber implements Closeable {
    private final FFmpegFrameGrabber grabber;
    private boolean started = false;
    private final Java2DFrameConverter converter = new Java2DFrameConverter();

    public VideoFrameGrabber(File video) {
        grabber = new FFmpegFrameGrabber(video);
    }

    public VideoFrameGrabber(String videoPath) {
        grabber = new FFmpegFrameGrabber(videoPath);
    }

    public void start() throws FFmpegFrameGrabber.Exception {
        if (!started) {
            grabber.start();
            started = true;
        }
    }

    public long getLengthInTime() throws FFmpegFrameGrabber.Exception {
        start();
        return grabber.getLengthInTime();
    }

    public Frame grabFrame() throws FFmpegFrameGrabber.Exception {
        start();
        Frame frame = grabber.grabImage();
        if (frame == null) {
            System.err.println("No frame grabbed.");
            return null;
        }

        return frame;
    }

    // grab first frame of a video and returns a converted image
    public BufferedImage grabFrameToImage() throws FFmpegFrameGrabber.Exception {

        return converter.getBufferedImage(grabFrame());
    }

    // grab the frame at a certain timestamp (in microseconds)
    public Frame grabFrameAtTimestamp(long timestamp) throws FFmpegFrameGrabber.Exception {
        start();

        grabber.setTimestamp(timestamp);

        Frame frame = grabber.grabImage();
        if (frame == null)
            System.err.println("No frame grabbed.");

        return frame;
    }

    // grab the frame at a certain timestamp (in microseconds) and returns a
    // converted image
    public BufferedImage grabFrameAtTimestampToImage(long timestamp) throws FFmpegFrameGrabber.Exception {
        return converter.getBufferedImage(grabFrameAtTimestamp(timestamp));
    }

    // saves the frame that is converted to image
    public void saveImage(BufferedImage image, String outputPath) {
        if (image == null) {
            System.err.println("Cannot save — image is null.");
            return;
        }
        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("Frame saved to: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error saving image to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (grabber != null) {
                if (started) {
                    try {
                        grabber.stop();
                    } catch (FFmpegFrameGrabber.Exception e) {
                        System.err.println("Warning: Grabber stop failed: " + e.getMessage());
                    }
                }
                grabber.release();
            }
        } catch (FFmpegFrameGrabber.Exception e) {
            System.err.println("Error releasing grabber: " + e.getMessage());
        } finally {
            started = false;
        }
    }
}
