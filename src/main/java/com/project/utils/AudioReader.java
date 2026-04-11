package com.project.utils;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;



public class AudioReader {
    private static String getExtension(String path) {
        int dotIndex = path.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new IllegalArgumentException("Invalid file: no extension");
        }
        return path.substring(dotIndex + 1).toLowerCase();
    }


    public static int[] read(String path) throws Exception {

        String extension = getExtension(path);

        switch (extension) {
            case "wav":
                return WavReader.readSamples(path);

            // future support
            // case "mp3":
            //     return Mp3Reader.readSamples(path);

            default:
                throw new UnsupportedOperationException(
                    "Unsupported audio format: " + extension
                );
        }
    }
   

    public static void main(String[] arg) {
// only testing :)
        try{
            
            int[] arr = WavReader.readSamples("./../../../../../../assets/audio/sample.wav");
            for(int x : arr){
                System.out.println(x);
            } 



        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}


class WavReader {

    public static int[] readSamples(String path) throws Exception {
        File file = new File(path);
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();

        byte[] bytes = stream.readAllBytes();

        int[] samples = new int[bytes.length / 2];

        int sampleIndex = 0;

        for (int i = 0; i < bytes.length; i += 2) {
            int sample = (bytes[i+1] << 8) | (bytes[i] & 0xFF);
            samples[sampleIndex++] = sample;
        }

        return samples;
    }
}



