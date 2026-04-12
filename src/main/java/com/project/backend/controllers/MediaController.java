package com.project.backend.controllers;

import com.project.core.MatchResult;
import com.project.core.UnifiedMatchResponse; // <-- IMPORT ADDED
import com.project.core.AudioFingerprint;
import com.project.core.VideoFingerprint;
import com.project.backend.utils.AudioHelper;
import com.project.backend.utils.VideoHelper;
import com.project.backend.utils.MovieHelper;
import com.project.media.Audio;
import com.project.media.Video;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

public class MediaController {

    private final MovieHelper movieDb = new MovieHelper();
    private final VideoHelper videoDb = new VideoHelper();
    private final AudioHelper audioDb = new AudioHelper();

    public void seedMedia(Context ctx) throws Exception {
        String title = ctx.formParam("title");
        UploadedFile uploadedFile = ctx.uploadedFile("media"); 

        if (uploadedFile == null || title == null) {
            ctx.status(400).result("Error: Missing media file or title parameter.");
            return;
        }

        String masterMovieId = movieDb.insertMovieAndGetId(title);
        if (masterMovieId == null) {
            ctx.status(500).result("Error: Could not generate Movie ID in database.");
            return;
        }

        File tempFile = File.createTempFile("seed_media_", uploadedFile.extension());
        Files.copy(uploadedFile.content(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try {
            System.out.println("Processing Audio and Video simultaneously for: " + title);

            CompletableFuture<Void> videoTask = CompletableFuture.runAsync(() -> {
                Video video = new Video(masterMovieId, title, tempFile);
                VideoFingerprint vFingerprint = video.generateFingerprint();
                videoDb.insertVideoHashes(vFingerprint.getFrames());
            });

            CompletableFuture<Void> audioTask = CompletableFuture.runAsync(() -> {
                Audio audio = new Audio(masterMovieId, title, tempFile);
                AudioFingerprint aFingerprint = audio.generateFingerprint();
                audioDb.insertAudioHashes(aFingerprint.getFrames());
            });

            CompletableFuture.allOf(videoTask, audioTask).join();

            ctx.status(201).result("Successfully seeded BOTH Audio and Video for: " + title + " (ID: " + masterMovieId + ")");

        } catch (Exception e) {
            ctx.status(500).result("Error during media processing: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.gc();
            if (tempFile.exists()) tempFile.delete();
        }
    }

    public void matchMedia(Context ctx) throws Exception {
        UploadedFile uploadedFile = ctx.uploadedFile("clip");

        if (uploadedFile == null) {
            ctx.status(400).result("Error: Missing clip file.");
            return;
        }

        File tempFile = File.createTempFile("query_media_", uploadedFile.extension());
        Files.copy(uploadedFile.content(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try {
            CompletableFuture<MatchResult> audioMatchTask = CompletableFuture.supplyAsync(() -> {
                Audio queryAudio = new Audio("query", "User Clip", tempFile);
                return audioDb.findBestMatch(queryAudio.generateFingerprint().getFrames());
            });

            CompletableFuture<MatchResult> videoMatchTask = CompletableFuture.supplyAsync(() -> {
                Video queryVideo = new Video("query", "User Clip", tempFile);
                return videoDb.findBestMatch(queryVideo.generateFingerprint().getFrames());
            });

            MatchResult audioResult = audioMatchTask.join();
            MatchResult videoResult = videoMatchTask.join();

            UnifiedMatchResponse finalResponse = new UnifiedMatchResponse(audioResult, videoResult);

            ctx.json(finalResponse);

        } catch (Exception e) {
            ctx.status(500).result("Error during media matching: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.gc();
            if (tempFile.exists()) tempFile.delete();
        }
    }
}