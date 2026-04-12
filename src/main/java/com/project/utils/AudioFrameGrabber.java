package com.project.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.io.Closeable;
import java.io.File;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class AudioFrameGrabber implements Closeable{
    private final FFmpegFrameGrabber grabber;
    private boolean started = false;

    public AudioFrameGrabber(File mediaFile) {
        this.grabber = new FFmpegFrameGrabber(mediaFile);
        this.grabber.setAudioChannels(1);
        this.grabber.setSampleRate(11025);
    }

    public double[] extractAllSamples() throws Exception {
        if (!started) {
            grabber.start();
            started = true;
        }

        double[] allSamples = new double[1000000]; 
        int totalSize = 0;
        Frame frame;

        while ((frame = grabber.grabSamples()) != null) {
            if (frame.samples != null && frame.samples[0] instanceof ShortBuffer) {
                ShortBuffer channelSamples = (ShortBuffer) frame.samples[0];
                int capacity = channelSamples.limit();

                if (totalSize + capacity > allSamples.length) {
                    allSamples = Arrays.copyOf(allSamples, allSamples.length * 2);
                }

                for (int i = 0; i < capacity; i++) {
                    allSamples[totalSize++] = channelSamples.get(i);
                }
            }
        }
        return Arrays.copyOf(allSamples, totalSize);
    }

    @Override
    public void close() {
        try {
            if (grabber != null) {
                if (started) { try { grabber.stop(); } catch (Exception ignored) {} }
                grabber.release();
            }
        } catch (Exception e) {
            System.err.println("Error releasing audio: " + e.getMessage());
        } finally {
            started = false;
        }
    }
}
