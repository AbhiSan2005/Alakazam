package com.project.backend.controllers;

import com.project.core.MatchResult;
import com.project.core.MovieMetaData;
import com.project.core.UnifiedMatchResponse;
import com.project.core.AudioFingerprint;
import com.project.core.VideoFingerprint;
import com.project.backend.utils.AudioHelper;
import com.project.backend.utils.VideoHelper;
import com.project.backend.utils.MovieHelper;
import com.project.media.Audio;
import com.project.media.Video;
import com.project.exceptions.*;
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

    private static final long MAX_FILE_SIZE_BYTES = 30_000_000;

    private boolean isAudioOnly(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".mp3") || lower.endsWith(".wav") ||
                lower.endsWith(".m4a") || lower.endsWith(".flac") ||
                lower.endsWith(".ogg") || lower.endsWith(".aac");
    }

    private boolean isVideoOnly(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".gif") || lower.endsWith(".mov") ||
                lower.endsWith(".avi") || lower.endsWith(".mkv") ||
                lower.endsWith(".wmv");
    }

    private boolean isStandardVideo(String filename) {
        return filename.toLowerCase().endsWith(".mp4");
    }

    public void seedMedia(Context ctx) throws Exception {
        String title = ctx.formParam("title");
        String genre = ctx.formParam("genre");
        String durationStr = ctx.formParam("duration");
        String yearStr = ctx.formParam("yearOfRelease");
        UploadedFile uploadedFile = ctx.uploadedFile("media");

        if (uploadedFile == null || title == null || genre == null || durationStr == null || yearStr == null) {
            ctx.status(400).result("Error: Missing media file or required metadata parameters.");
            return;
        }

        String filename = uploadedFile.filename();

        if (!isAudioOnly(filename) && !isVideoOnly(filename) && !isStandardVideo(filename)) {
            throw new UnsupportedMediaFormatException("The file format for '" + filename + "' is not supported.");
        }

        int duration = Integer.parseInt(durationStr);
        int yearOfRelease = Integer.parseInt(yearStr);

        String masterMovieId = movieDb.insertMovieAndGetId(title, genre, duration, yearOfRelease);
        if (masterMovieId == null) {
            ctx.status(500).result("Error: Could not generate Movie ID in database.");
            return;
        }

        File tempFile = File.createTempFile("seed_media_", uploadedFile.extension());
        Files.copy(uploadedFile.content(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        boolean audioOnly = isAudioOnly(filename);
        boolean videoOnly = isVideoOnly(filename);

        try {
            System.out.println("Processing media for: " + title);

            CompletableFuture<Void> videoTask = audioOnly
                    ? CompletableFuture.completedFuture(null)
                    : CompletableFuture.runAsync(() -> {
                        Video video = new Video(masterMovieId, title, tempFile);
                        VideoFingerprint vFingerprint = video.generateFingerprint();
                        videoDb.insertVideoHashes(vFingerprint.getFrames());
                    });

            CompletableFuture<Void> audioTask = videoOnly
                    ? CompletableFuture.completedFuture(null)
                    : CompletableFuture.runAsync(() -> {
                        Audio audio = new Audio(masterMovieId, title, tempFile);
                        AudioFingerprint aFingerprint = audio.generateFingerprint();
                        audioDb.insertAudioHashes(aFingerprint.getFrames());
                    });

            CompletableFuture.allOf(videoTask, audioTask).join();
            ctx.status(201).result("Successfully seeded media for: " + title + " (ID: " + masterMovieId + ")");

        } finally {
            System.gc();
            if (tempFile.exists())
                tempFile.delete();
        }
    }

    public void matchMedia(Context ctx) throws Exception {
        UploadedFile uploadedFile = ctx.uploadedFile("clip");

        if (uploadedFile == null) {
            ctx.status(400).result("Error: Missing clip file.");
            return;
        }

        // DURATION/SIZE CHECK
        if (uploadedFile.size() > MAX_FILE_SIZE_BYTES) {
            throw new MediaDurationExceededException(
                    "Clip exceeds the maximum allowed size limit. Please trim to 30 MB or less.");
        }

        String filename = uploadedFile.filename();

        if (!isAudioOnly(filename) && !isVideoOnly(filename) && !isStandardVideo(filename)) {
            throw new UnsupportedMediaFormatException("The file format for '" + filename + "' is not supported.");
        }

        File tempFile = File.createTempFile("query_media_", uploadedFile.extension());
        Files.copy(uploadedFile.content(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        boolean audioOnly = isAudioOnly(filename);
        boolean videoOnly = isVideoOnly(filename);

        try {
            CompletableFuture<MatchResult> audioMatchTask = videoOnly
                    ? CompletableFuture.completedFuture(new MatchResult("No Match Found", 0.0, 0, 0))
                    : CompletableFuture.supplyAsync(() -> {
                        Audio queryAudio = new Audio("query", "User Clip", tempFile);
                        return audioDb.findBestMatch(queryAudio.generateFingerprint().getFrames());
                    });

            CompletableFuture<MatchResult> videoMatchTask = audioOnly
                    ? CompletableFuture.completedFuture(new MatchResult("No Match Found", 0.0, 0, 0))
                    : CompletableFuture.supplyAsync(() -> {
                        Video queryVideo = new Video("query", "User Clip", tempFile);
                        return videoDb.findBestMatch(queryVideo.generateFingerprint().getFrames());
                    });

            MatchResult audioResult = audioMatchTask.join();
            MatchResult videoResult = videoMatchTask.join();

            UnifiedMatchResponse finalResponse = new UnifiedMatchResponse(audioResult, videoResult);

            if (!finalResponse.mediaId.equals("No Match Found")) {
                MovieMetaData meta = movieDb.getMovieDetails(finalResponse.mediaId);
                finalResponse.applyMetadata(meta);
            }

            ctx.json(finalResponse);

        } finally {
            System.gc();
            if (tempFile.exists())
                tempFile.delete();
        }
    }
}