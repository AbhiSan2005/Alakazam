package com.project.backend.routes;

import com.project.backend.controllers.VideoController;
import io.javalin.Javalin;

public class VideoRoutes {
    public static void registerRoutes(Javalin app) {
        VideoController videoController = new VideoController();

        app.post("/api/video/seed", ctx -> videoController.seedMovie(ctx));
        app.post("/api/video/match", ctx -> videoController.matchClip(ctx));
    }
}