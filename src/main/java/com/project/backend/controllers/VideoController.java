package com.project.backend.controllers;

import com.project.core.MatchResult;
import com.project.core.VideoFingerprint;
import com.project.backend.utils.VideoHelper;
import com.project.media.Video;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.nio.file.Files;

public class VideoController {

    private final VideoHelper repository = new VideoHelper();

    public void seedMovie(Context ctx) throws Exception {
        String title = ctx.formParam("title");
        UploadedFile uploadedFile = ctx.uploadedFile("video");

        if (uploadedFile == null || title == null) {
            ctx.status(400).result("Error: Missing video file or title parameter.");
            return;
        }

        String generatedShortId = repository.insertMovieAndGetId(title);
        
        if (generatedShortId == null) {
            ctx.status(500).result("Error: Could not generate Movie ID in database.");
            return;
        }

        File tempFile = File.createTempFile("seed_", ".mp4");
        Files.write(tempFile.toPath(), uploadedFile.content().readAllBytes());

        Video video = new Video(generatedShortId, title, tempFile);
        VideoFingerprint fingerprint = video.generateFingerprint();

        // System.out.println(fingerprint.getFrames());

        repository.insertVideoHashes(fingerprint.getFrames());

        tempFile.delete();
        ctx.status(201).result("Successfully seeded movie: " + title + " (ID: " + generatedShortId + ")");
    }

    public void matchClip(Context ctx) throws Exception {
        UploadedFile uploadedFile = ctx.uploadedFile("clip");

        if (uploadedFile == null) {
            ctx.status(400).result("Error: Missing clip file.");
            return;
        }

        File tempFile = File.createTempFile("query_", ".mp4");
        Files.write(tempFile.toPath(), uploadedFile.content().readAllBytes());

        Video queryVideo = new Video("query", "User Clip", tempFile);
        VideoFingerprint fingerprint = queryVideo.generateFingerprint();

        // System.out.println(fingerprint.getFrames());

        MatchResult result = repository.findBestMatch(fingerprint.getFrames());

        tempFile.delete();
        ctx.json(result); 
    }
}
