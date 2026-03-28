package com.project.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.opencv_img_hash.PHash;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//grab and save frames to image files
public class VideoFrameGrabber {
    private static final String VIDEO_PATH = "assets/video/testVideo.mp4";
    private static final String OUTPUT_PATH = "assets/frames/testVideoFrames/output.png";

    //grab first frame of a video and returns a converted image
    public BufferedImage grabFrameToImage() {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(VIDEO_PATH)) {
            grabber.start();

            Frame frame = grabber.grabImage();
            if (frame == null) {
                System.err.println("No frame grabbed.");
                return null;
            }

            // Convert to BufferedImage BEFORE release — safe to pass around
            Java2DFrameConverter converter = new Java2DFrameConverter();
            return converter.getBufferedImage(frame);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //grab the frame at a certain timestamp (in microseconds) and returns a converted image
    public BufferedImage grabFrameAtTimestampToImage(long timestamp) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(VIDEO_PATH)) {
            grabber.start();

            grabber.setTimestamp(timestamp);

            Frame frame = grabber.grabImage();
            if (frame == null) {
                System.err.println("No frame grabbed.");
                return null;
            }

            // Convert to BufferedImage BEFORE release — safe to pass around
            Java2DFrameConverter converter = new Java2DFrameConverter();
            return converter.getBufferedImage(frame);

        } catch (FrameGrabber.Exception e) {
            System.out.println("Error grabbing frame: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //saves the frame that is converted to image
    public void saveImage(BufferedImage image) {
        if (image == null) {
            System.err.println("Cannot save — image is null.");
            return;
        }
        try {
            ImageIO.write(image, "png", new File(OUTPUT_PATH));
            System.out.println("Frame saved to: " + OUTPUT_PATH);
        } catch (IOException e) {
            System.out.println("Error saving image to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
