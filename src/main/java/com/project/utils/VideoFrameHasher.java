package com.project.utils;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_img_hash.PHash;

import java.awt.image.BufferedImage;

//converting frames to hashes using pHash
public class VideoFrameHasher {
    private final Java2DFrameConverter imageConverter = new Java2DFrameConverter();
    private final OpenCVFrameConverter.ToMat frameConverter = new OpenCVFrameConverter.ToMat();
    private final PHash pHash = PHash.create();

    public long hashFrame (BufferedImage image) {
        Frame frame = imageConverter.getFrame(image); //convert image to frame
        Mat mat = frameConverter.convert(frame); //convert frame to mat
        Mat hash = new Mat();
        pHash.compute(mat, hash);
        return matToLong(hash);
    }

    //convert 1 x 8 mat to 64 bit long
    //each value of mat represents a byte i.e. 8 bit
    private long matToLong(Mat hash) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            long byteVal = hash.ptr(0).get(i) & 0xFF;  // mask: signed byte → unsigned
            result |= byteVal << (i * 8);                  // shift into correct position
        }
        return result;
    }

}
